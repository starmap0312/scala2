import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.ssl.TrustStrategy

import scala.io.Source

object HttpAsyncClientTest extends App {

  val acceptingTrustStrategy = new TrustStrategy() {
    override def isTrusted(chain: Array[X509Certificate], authType: String): Boolean = true
  }
  val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
  sslContext.init(null, null, null)

  val client = HttpAsyncClients.custom.setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).setSSLContext(sslContext).build
  client.start()

  val request = new HttpGet("https://www.google.com/")
  val future = client.execute(request, null)
  val response = future.get

  val result = Source.fromInputStream(response.getEntity.getContent).getLines().mkString("\n")
  client.close()
  println(result)
}
