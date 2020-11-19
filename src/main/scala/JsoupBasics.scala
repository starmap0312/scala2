import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.io.Source

object JsoupBasics extends App {
//  val url = new URL("https://golang.org/dl/go1.15.5.darwin-amd64.pkg")
//  val html = Source.fromURL("https://s.yimg.com/fz/api/res/1.2/.Mlpe7yPohi6RZt2EmCJOQ--~C/YXBwaWQ9c3JjaGRkO2ZpPWZpbGw7aD05MjtweG9mZj0wO3B5b2ZmPTA7cT04MDtzbT0xO3c9MTY0/https://media.zenfs.com/en/elle_570/bcff6bc4501450a492a3bf7a24b46b3f")
////  val x = Jsoup.parse(url, 10000)
//  val input = html
//  println(input)
  val x: Document = Jsoup.connect("https://video.media.yql.yahoo.com/v1/video/sapi/hlsstreams/c97b8243-5fa1-3beb-82cc-869f7d6f64ee.m3u8?site=tv&region=US&lang=en&devtype=desktop&src=sapi").ignoreContentType(true).get()
  println(x.text())

  val y: Document = Jsoup.connect("https://s.yimg.com/hd/cp-video-transcode/prod/2020-11/19/5fb60fafe61260517927b3ae/5fb60fafe61260517927b3af_o_U_v2.jpg").ignoreContentType(true).get()
  val z = Jsoup.parse((y.outerHtml()))
  println(z.head())
//  println(z.body())

}
