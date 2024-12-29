package tool.dbencrypter.github

import com.google.gson.Gson
import com.google.gson.JsonObject
import core.model.HOVerwaltung
import core.util.HOLogger
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
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

		val builder: OkHttpClient.Builder = OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager)

		return builder.build()
	}


	data class GitHubIssue(
		val title: String,
		val body: String
	)


	private fun createGitHubIssue(token: String, owner: String, repo: String, title: String, body: String) {
		val url = "https://api.github.com/repos/$owner/$repo/issues"
		val issue = GitHubIssue(title, body)

		val gson = Gson()
		val json = gson.toJson(issue)

		val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
		val client = OkHttpClient()

		val request = Request.Builder()
			.url(url)
			.header("Authorization", "Bearer $token")
			.header("Accept", "application/vnd.github+json")
			.post(requestBody)
			.build()

		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, e: IOException) {
				println("Failed to create issue: ${e.message}")
			}

			override fun onResponse(call: Call, response: Response) {
				if (response.isSuccessful) {
					println("Issue created successfully: ${response.body?.string()}")
				} else {
					println("Failed to create issue: ${response.code} - ${response.body?.string()}")
				}
			}
		})
	}

	fun requestDeviceCode() {
		val httpClient = initializeHttpsClient()
		val parameters = URLEncoder.encode("Iv23lif3sEVL0KxRh6Mw", StandardCharsets.UTF_8)
		val formBody: RequestBody = FormBody.Builder()
			.add("client_id", parameters)
			.build()
		val request: Request = Request.Builder()
			.url("https://github.com/login/device/code")
			.addHeader("Accept", "application/json")
			.post(formBody)
			.build()

		val response = httpClient.newCall(request).execute()
		if (response.isSuccessful) {
			val bodyAsString = response.body?.string()
			val gson = Gson()
			val responseAsJson = gson.fromJson(bodyAsString, JsonObject::class.java)
			println(responseAsJson.asMap())

			// Get device code
			val deviceCode: String = responseAsJson.getAsJsonPrimitive("device_code").asString

			val accessTokenBody: RequestBody = FormBody.Builder()
				.add("client_id", parameters)
				.add("device_code", URLEncoder.encode(deviceCode, StandardCharsets.UTF_8))
				.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
				.build()
			val requestAccessToken = Request.Builder()
				.url("https://github.com/login/oauth/access_token")
				.addHeader("Accept", "application/json")
				.post(accessTokenBody)
				.build()

			var accessToken = ""

			// Until you have authorized the app in the browser, using the User code displayed
			// in the logs, the app waits, in a busy-wait loop.
			while (true) {
				val accessTokenResponse = httpClient.newCall(requestAccessToken).execute()
				val tokenResponse = gson.fromJson(accessTokenResponse.body?.string(), JsonObject::class.java)
				if (tokenResponse.has("access_token")) {
					accessToken = tokenResponse.getAsJsonPrimitive("access_token").asString
					break
				} else if (tokenResponse.has("error")) {
					//
					val errorVal = tokenResponse.getAsJsonPrimitive("error").asString
					println(tokenResponse.asMap())
					if (errorVal == "authorization_pending") {
						Thread.sleep(5_000)
					} else if (errorVal == "slow_down") {
						Thread.sleep(10_000)
					} else {
						HOLogger.instance().error(javaClass, "Unrecoverable error: ${errorVal}")
						break
					}
				}
			}
			println("Access Code: $accessToken")
			createGitHubIssue(
				accessToken,
				"ho-dev",
				"HattrickOrganizer",
				"[BUG] Test",
				"Github App Test (Ignore)"
			)
		}
	}
}


fun main() {
	HOVerwaltung.instance().loadLatestHoModel()
	val githubApp = GithubApp()
	githubApp.requestDeviceCode()
}

