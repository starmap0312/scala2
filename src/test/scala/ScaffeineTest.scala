import com.github.blemale.scaffeine.{ Cache, Scaffeine }
import scala.concurrent.duration._

object ScaffeineTest {

  def main(args: Array[String]): Unit = {
    val cache: Cache[Int, String] =
      Scaffeine()
        .recordStats()
        .expireAfterWrite(1.seconds)
        .maximumSize(3)
        .build[Int, String]()

    cache.put(1, "foo")

    println(cache.getIfPresent(1)) // cache hit: Some("foo")
    println(cache.getIfPresent(2)) // cache miss: None
    println(cache.asMap.size) // 1
    println(cache.asMap.toString()) // Map(1 -> foo)
    println(cache.stats())
    // requries: Scaffeine().recordStats()
    // CacheStats{hitCount=1, missCount=1, loadSuccessCount=0, loadFailureCount=0, totalLoadTime=0, evictionCount=0, evictionWeight=0}

    println
    Thread.sleep(3000)
    cache.cleanUp()

    println(cache.getIfPresent(1)) // cache hit: None
    println(cache.getIfPresent(2)) // cache miss: None
    println(cache.asMap.size) // 0 (need to call cleanup(), otherwise, it size=1)
    println(cache.asMap.toString()) // Map(1 -> foo)
    println(cache.stats())
    // requries: Scaffeine().recordStats()
    // CacheStats{hitCount=1, missCount=3, loadSuccessCount=0, loadFailureCount=0, totalLoadTime=0, evictionCount=0, evictionWeight=0}

  }
}
