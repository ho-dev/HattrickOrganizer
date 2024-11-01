package tool.dbencrypter.github

import core.model.HOVerwaltung
import okhttp3.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.*
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class GithubApp {
	@Throws(Exception::class)
	private fun initializeHttpsClient(): OkHttpClient {
		val keystoreCred = String(Base64.getDecoder().decode("aGVsbG9oYXR0cmljaw==")).toCharArray()
		val trustStoreStream = this.javaClass.classLoader.getResourceAsStream("truststore.jks")

		val keystore = KeyStore.getInstance("JKS")
		keystore.load(trustStoreStream, keystoreCred)

		val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
		keyManagerFactory.init(keystore, keystoreCred)
		val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
		trustManagerFactory.init(keystore)

		val sslContext = SSLContext.getInstance("TLSv1.2")
		sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)

		val sslSocketFactory = sslContext.socketFactory
		val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager

		val proxyPort = 3000
		val proxyHost = "localhost"

		var builder: OkHttpClient.Builder = OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager)

//		if (ModuleConfig.instance().getBoolean("PromotionStatus_DebugProxy", false)) {
//			builder = builder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort)))
//		}

		return builder.build()
	}

	fun requestDeviceCode() {
		val httpClient = initializeHttpsClient()
		/*
		 uri = URI("https://github.com/login/device/code")
  parameters = URI.encode_www_form("client_id" => CLIENT_ID)
  headers = {"Accept" => "application/json"}

  response = Net::HTTP.post(uri, parameters, headers)
  parse_response(response)
		 */

		val parameters = URLEncoder.encode("Iv23lif3sEVL0KxRh6Mw", StandardCharsets.UTF_8.toString())
		val formBody: RequestBody = FormBody.Builder()
			.add("client_id", parameters)
			.build()
		val request: Request = Request.Builder()
			.url("https://github.com/login/device/code")
			.addHeader("Accept", "application/json")
			.post(formBody)
			.build()

		val response = httpClient.newCall(request).execute()
		println(response.code)
		println(response.body?.string())
	}
}


fun main() {
	HOVerwaltung.instance().loadLatestHoModel()
	val githubApp = GithubApp()
	githubApp.requestDeviceCode()
}

