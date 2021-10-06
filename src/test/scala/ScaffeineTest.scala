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
    println(cache.stats().evictionCount()) // 0: number of times an entry has been evicted
    println(cache.stats().loadSuccessCount()) // 0: number of times cache lookup methods have successfully loaded a new value
    println(cache.stats().loadFailureCount()) // 0: number of times cachelookup methods failed to load a new value (i.e. no value was found or an exception thrown)
    println(cache.stats().loadCount()) // 0: number of times that cache lookup methods attempted to load new values
    println(cache.stats().totalLoadTime()) // 0: the nanoseconds the cache has spent loading new values, used to calculate the miss penalty
    println(cache.stats().hitRate()) // 0.5
    println(cache.estimatedSize()) // 1
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
    println(cache.stats().evictionCount()) // 1: number of times an entry has been evicted
    println(cache.stats().loadSuccessCount()) // 0: number of times cache lookup methods have successfully loaded a new value
    println(cache.stats().loadFailureCount()) // 0: number of times cachelookup methods failed to load a new value (i.e. no value was found or an exception thrown)
    println(cache.stats().loadCount()) // 0: number of times that cache lookup methods attempted to load new values
    println(cache.stats().totalLoadTime()) // 0: the nanoseconds the cache has spent loading new values, used to calculate the miss penalty
    println(cache.stats().hitRate()) // 0.25
    println(cache.estimatedSize()) // 0 (need to call cleanup(), otherwise, its size=1)
    println(cache.asMap.size) // 0 (need to call cleanup(), otherwise, its size=1)
    println(cache.asMap.toString()) // Map(1 -> foo)
    println(cache.stats())
    // requries: Scaffeine().recordStats()
    // CacheStats{hitCount=1, missCount=3, loadSuccessCount=0, loadFailureCount=0, totalLoadTime=0, evictionCount=0, evictionWeight=0}

  }
}
