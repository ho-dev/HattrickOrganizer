package tool.dbencrypter.github

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import core.model.UserParameter
import core.util.BrowserLauncher
import core.util.HOLogger
import core.util.StringUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
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
					// TODO Handle 401 {"message":"Bad credentials","documentation_url":"https://docs.github.com/rest","status":"401"}
					println("Failed to create issue: ${response.code} - ${response.body?.string()}")
				}
			}
		})
	}

	fun requestDeviceCode(title: String, body: String, codePrompt: (String) -> Unit) {
		// FIXME UserParameter should be passed to this class, rather than using singleton.
		//val githubAccessToken = UserParameter.instance().githubAccessToken
		val githubAccessToken = ""
		var accessToken = ""

		if (StringUtils.isEmpty(githubAccessToken)) {
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
				val verificationUrl = responseAsJson.getAsJsonPrimitive("verification_uri").asString
				val deviceCode: String = responseAsJson.getAsJsonPrimitive("device_code").asString
				val userCode: String = responseAsJson.getAsJsonPrimitive("user_code").asString
				BrowserLauncher.openURL(verificationUrl)
				codePrompt(userCode) // This will block until user confirms.

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


				// Until you have authorized the app in the browser, using the User code displayed
				// in the logs, the app waits, in a busy-wait loop.
				while (true) {
					val accessTokenResponse = httpClient.newCall(requestAccessToken).execute()
					val tokenResponse = gson.fromJson(accessTokenResponse.body?.string(), JsonObject::class.java)
					if (tokenResponse.has("access_token")) {
						accessToken = tokenResponse.getAsJsonPrimitive("access_token").asString
						// DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'githubAccessToken'
						UserParameter.instance().githubAccessToken = accessToken
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
							HOLogger.instance().error(javaClass, "Unrecoverable error: $errorVal")
							break
						}
					}
				}
			}
		} else {
			accessToken = githubAccessToken
		}

		// TODO Remove when done
		println("Access Code: $accessToken")
		/*
		createGitHubIssue(
			accessToken,
			"ho-dev",
			"HattrickOrganizer",
			"[BUG] Test",
			"Github App Test (Ignore)"
		)
		 */
		createGitHubIssue(
			accessToken,
			"ho-dev",
			"test-ghapp",
			"[BUG] $title",
			"$body (Ignore)"
		)
	}

	fun uploadFile(pathToFile: File, issue: String, owner: String, repo: String, token: String) {
		val branch = "attach"
//		val url = "https://github.com/$owner/$repo/issues/8/comments"
//
//		val client = OkHttpClient()
//		val file = pathToFile
//		val mediaType = "application/octet-stream".toMediaTypeOrNull()
//		val requestBody = MultipartBody.Builder()
//			.setType(MultipartBody.FORM)
//			.addFormDataPart("file", file.name, RequestBody.create(mediaType, file))
//			.build()
//
//		val request = Request.Builder()
//			.url("https://uploads.github.com/repos/$owner/$repo/issues/$issue/comments")
//			.post(requestBody)
//			.addHeader("Authorization", "token $token")
//			.addHeader("Content-Type", "application/json")
//			.build()
//
//		client.newCall(request).execute().use { response ->
//			if (!response.isSuccessful) throw IOException("Unexpected code $response")
//			val responseBody = response.body?.string()
//			println("File uploaded successfully: $responseBody")
//		}

		// val uploadUrl = "https://api.github.com/repos/$owner/$repo/contents/${pathToFile.name}"
		// val uploadRequestBody = uploadJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
		// val uploadRequest = Request.Builder()
		// 	.url(uploadUrl)
		// 	.addHeader("Authorization", "Bearer $GITHUB_TOKEN")
		// 	.addHeader("Accept", "application/vnd.github.v3+json")
		// 	.put(uploadRequestBody)
		// 	.build()

		val client = OkHttpClient()
		val mediaType = "application/json; charset=utf-8".toMediaType()

		try {
			// Read file content and encode it in Base64.
			val fileBytes = pathToFile.readBytes()
			val encodedContent = Base64.getEncoder().encodeToString(fileBytes)
			val fileName = pathToFile.name
			val currentTime = System.currentTimeMillis()

			// Create a unique remote path for the attachment.
			val remotePath = "issue-attachments/$issue/${currentTime}-$fileName"
			val uploadUrl = "https://api.github.com/repos/$owner/$repo/contents/$remotePath"

			// Build the JSON payload for the file upload, specifying the target branch.
			val payload = """
            {
              "message": "Upload attachment for issue #$issue: $fileName",
			  "committer": {"name": "tychobrailleur", "email": "upload@example.com"},
              "content": "$encodedContent",
              "branch": "$branch"
            }
        """.trimIndent()

			val requestBody = RequestBody.create(mediaType, payload)

			// Create the upload request with additional headers.
			val uploadRequest = Request.Builder()
				.url(uploadUrl)
				.header("Authorization", "Bearer $token")
				.header("Accept", "application/vnd.github+json")
				.header("User-Agent", "HO")
				.put(requestBody)
				.build()

			// Execute the file upload request.
			client.newCall(uploadRequest).execute().use { response ->
				// Read full response body.
				val responseBodyStr = response.body?.string() ?: "No response body"

				// Display full response even if the request fails.
				if (!response.isSuccessful) {
					println(
						"File upload failed:\n" +
							"Status: ${response.code} ${response.message}\n" +
							"Headers: ${response.headers}\n" +
							"Body: $responseBodyStr"
					)
					return
				}
				// Also print the full response on success.
				println(
					"File upload response:\n" +
						"Status: ${response.code} ${response.message}\n" +
						"Headers: ${response.headers}\n" +
						"Body: $responseBodyStr"
				)

				// Parse the response to extract the download URL.
				val jsonResponse = JsonParser.parseString(responseBodyStr).asJsonObject
				val contentObj = jsonResponse.getAsJsonObject("content")
				val downloadUrl = contentObj.get("download_url").asString

				// Build the comment payload with a link to the uploaded file.
				val commentUrl = "https://api.github.com/repos/$owner/$repo/issues/$issue/comments"
				val commentPayload = """
                {
                  "body": "Attachment uploaded on branch '$branch': $downloadUrl"
                }
            """.trimIndent()
				val commentRequestBody = RequestBody.create(mediaType, commentPayload)
				val commentRequest = Request.Builder()
					.url(commentUrl)
					.header("Authorization", "Bearer $token")
					.header("Accept", "application/vnd.github+json")
					.header("User-Agent", "HO")
					.post(commentRequestBody)
					.build()

				// Execute the comment creation request.
				client.newCall(commentRequest).execute().use { commentResponse ->
					val commentResponseBody = commentResponse.body?.string() ?: "No response body"
					if (!commentResponse.isSuccessful) {
						println(
							"Comment creation failed:\n" +
								"Status: ${commentResponse.code} ${commentResponse.message}\n" +
								"Headers: ${commentResponse.headers}\n" +
								"Body: $commentResponseBody"
						)
					} else {
						println(
							"Comment creation response:\n" +
								"Status: ${commentResponse.code} ${commentResponse.message}\n" +
								"Headers: ${commentResponse.headers}\n" +
								"Body: $commentResponseBody"
						)
					}
				}
			}
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
	}
}
