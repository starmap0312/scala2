package sbe

import com.my.sbe._
import org.agrona.concurrent.UnsafeBuffer
import java.nio.ByteBuffer
import scala.collection.JavaConverters._

// https://aeroncookbook.com/simple-binary-encoding/basic-sample/
// ref: https://github.com/real-logic/simple-binary-encoding
// wiki: https://github.com/real-logic/simple-binary-encoding/wiki
// Java Users Guide: https://github.com/real-logic/simple-binary-encoding/wiki/Java-Users-Guide
// SBE:
//   an OSI layer 6 presentation for encoding and decoding binary application messages for low-latency applications
//     it is used to encode and decode SBE messages in binary format based on schemas
//     it is used as a library to enable on-the-fly decoding of messages, or to generate compilable stubs to directly encode or decode messages
// Low Latency:
//   SBE codec aims to achieve low-latency with minimal variance (i.e. low p95/p99) by:
//     Copy-Free: no intermediate buffers for the encoding or decoding of messages
//       i.e. encode and decode directly to/from the underlying buffer
//       imitation: messages greater in size than the transfer buffers are not directly supported
//     Native Type Mapping: encode the data as native types in the underlying buffer
//       ex. a 64-bit integer can be encoded directly to the underlying buffer as a single x86_64 MOV assembly instruction
//       so the fields can be accessed for a similar cost to class or struct fields in a high-level language, ex. C++ or Java
//     Allocation-Free: SBE codecs employ the flyweight pattern (i.e. it creates and manages flyweights in a "pool")
//       allocation of objects results in CPU cache churn which reduces efficiency
//       allocated objects then have to be collected and deleted (Java garbage collector has to do this by a stop-the-world pause)
//       the flyweight windows over the underlying buffer for direct encoding and decoding of messages
//     Streaming Access: a streaming based approach that addresses memory in an ascending sequential access pattern
//       designed to encode and decode messages based on a forward progression of the position in the underlying buffer
//       it is possible to backtrack to a degree within messages but this is highly discouraged from a performance and latency perspective
//     Word Aligned Access: prevent words accessed on non word sized boundaries
//       i.e. the starting address of a word should be a multiple of its size in bytes
//            64-bit integers should only begin on byte address divisible by 8
//            32-bit integers should only begin on byte addresses divisible by 4
//       SBE schemas support the concept of an offset that defines the starting position of a field within a message
//       it is assumed the messages are encapsulated within a framing protocol on 8 byte boundaries
object SBEBasics extends App {
  // 1) Encoding
  // construct the two encoders required
  val encoder: SampleMessageEncoder = new SampleMessageEncoder()
  println(s"encoder.encodedLength=${encoder.encodedLength}") // encoder.encodedLength=0

  // 1.1) build the message header and actual header objects: this can be done once and reused if desired
  val headerEncoder: MessageHeaderEncoder = new MessageHeaderEncoder()

  // 1.2) allocate memory for the encoded data to be written to
  //      allocate memory and an unsafe buffer to act as a destination for the data
  val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(128)
  val directBuffer: UnsafeBuffer = new UnsafeBuffer(byteBuffer)

  // 1.3) wrap the buffer, and apply the header
  //      so we then can use the encoder to write the encoded output to the direct buffer

  encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder)
  println(s"encoder.encodedLength=${encoder.encodedLength}") // encoder.encodedLength=16
  // MessageFlyweight.encodedLength():
  //   the method returns the current encoded length in bytes, i.e. how far it has progressed

  // 1.4) set the data fields as desired
  //      use the encoder to set the fields to desired values
  encoder.sequence(123)
  encoder.enumField(SampleEnum.VALUE_1)
  encoder.message("a message")
  encoder.composite().field1(10)
  encoder.composite().field2(20)
  // single fixed fields can be encoded in a fluent style after a message flyweight has been reset for encoding
  // i.e. encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder)
  //      .sequence(123)
  //      .enumField(SampleEnum.VALUE_1)
  //      .message("a message")

  // encoding repeating groups
  //   first stage the count of times the group will repeat
  val groupEncoder: SampleMessageEncoder.GroupEncoder = encoder.groupCount(2)
  //   then use the next() method to cursor forward while encoding
  groupEncoder.next()
    .groupField1(1)
    .groupField2(2)
    .groupField3("group1")
  groupEncoder.next()
    .groupField1(3)
    .groupField2(4)
    .groupField3("group2")


  println(s"encoder.encodedLength=${encoder.encodedLength}") // encoder.encodedLength=61

  val encodedLength = MessageHeaderEncoder.ENCODED_LENGTH + encoder.encodedLength // encodedLength=33
  println(s"encodedLength=$encodedLength") // 69

  // 2) Decoding
  // 2.1) construct the decoder and message header decoder
  val decoder = new SampleMessageDecoder
  val headerDecoder = new MessageHeaderDecoder

  // 2.2) the header decoder wraps the inbound buffer, and reads the header
  var bufferOffset: Int = 0
  headerDecoder.wrap(directBuffer, bufferOffset)

  // 2.3) safety check can be done at this point to verify that the correct decoder is being used
  //      lookup the applicable flyweight to decode this type of message based on templateId and version
  val templateId = headerDecoder.templateId
  if (templateId != SampleMessageDecoder.TEMPLATE_ID) throw new IllegalStateException("Template ids do not match")
  val actingBlockLength: Int = headerDecoder.blockLength
  val actingVersion: Int = headerDecoder.version
  println(s"templateId, actingBlockLength, actingVersion=$templateId, $actingBlockLength, $actingVersion") // templateId, actingBlockLength, actingVersion=1, 16, 1

  // 2.4) next, wrap the inbound buffer

  bufferOffset += headerDecoder.encodedLength
  println(s"bufferOffset: $bufferOffset") // bufferOffset: 8
  decoder.wrap(directBuffer, bufferOffset, actingBlockLength, actingVersion)

  println(s"decoder.sequence=${decoder.sequence}") // decoder.sequence=123
  println(s"decoder.enumField=${decoder.enumField}") // decoder.enumField=VALUE_1
  println(s"decoder.composite.field1=${decoder.composite.field1}") // decoder.composite.field1=10
  println(s"decoder.composite.field2=${decoder.composite.field2}") // decoder.composite.field2=20
  println(s"decoder.message=${decoder.message}") // decoder.message=a message
  val groups: Iterator[SampleMessageDecoder.GroupDecoder] = decoder.group().iterator().asScala
  for (g <- groups) {
    println(s"g.groupField1=${g.groupField1}")
    println(s"g.groupField2=${g.groupField2}")
    println(s"g.groupField3=${g.groupField3}")
    // g.groupField1=1
    // g.groupField2=2
    // g.groupField3=group1
    // g.groupField1=3
    // g.groupField2=4
    // g.groupField3=group2
  }
}
