import com.github.blemale.scaffeine.{ Cache, Scaffeine }
import scala.concurrent.duration._

object ScaffeineTest {

  def main(args: Array[String]): Unit = {
    val cache: Cache[Int, String] =
      Scaffeine()
        .recordStats()
        .expireAfterWrite(3.seconds)
        .maximumSize(3)
        .build[Int, String]()

    cache.put(1, "foo")

    println(cache.getIfPresent(1)) // cache hit: Some("foo")
    println(cache.getIfPresent(2)) // cache miss: None
    println(cache.stats())

    Thread.sleep(3100)

    println(cache.getIfPresent(1)) // cache hit: None
    println(cache.getIfPresent(2)) // cache miss: None
    println(cache.stats())

  }
}
