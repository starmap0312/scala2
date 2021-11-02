package sbe
// https://github.com/real-logic/simple-binary-encoding
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
  println("hello")
}
