import java.security.cert.X509Certificate

import javax.net.ssl.SSLContext
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.concurrent.FutureCallback
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.ssl.TrustStrategy

import scala.concurrent.{Await, Future, Promise}
import scala.io.Source
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object HttpAsyncClientTest2 extends App {

  val client = Future {
    val acceptingTrustStrategy = new TrustStrategy() {
      override def isTrusted(chain: Array[X509Certificate], authType: String): Boolean = true
    }
    val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
    sslContext.init(null, null, null)

    val client = HttpAsyncClients.custom.setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).setSSLContext(sslContext).build
    client.start()
    client
  }

  val futureResponse = client.flatMap {
    client =>
      val response = Promise[HttpResponse]()
      client.execute(new HttpGet("https://www.google.com/"), new FutureCallback[HttpResponse] {
        override def cancelled(): Unit = response.failure(new Exception("cancelled"))
        override def completed(result: HttpResponse): Unit = response.complete(Success(result))
        override def failed(ex: Exception): Unit = response.failure(ex)
      })
      response.future.map(_ -> client)
  }.map {
    case (response, client) =>
      val result = Source.fromInputStream(response.getEntity.getContent).getLines().mkString("\n")
      client.close()
      result
  }

  val result = Await.result(futureResponse, 5.seconds)
  println(result)
}
