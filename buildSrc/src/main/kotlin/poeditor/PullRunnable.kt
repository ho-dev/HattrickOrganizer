package de.jansauer.poeditor

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.gradle.workers.WorkAction
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit

abstract class PullRunnable : WorkAction<PullWorkParameters> {
    private val logger = LoggerFactory.getLogger(PullRunnable::class.java)
    override fun execute() {
        val parameters = parameters
        logger.info("Pulling language '{}' from project '{}'", parameters.lang.get(), parameters.projectId.get())
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val body: RequestBody = FormBody.Builder()
            .add("api_token", parameters.apiKey.get() ?: "")
            .add("id", parameters.projectId.get() ?: "")
            .add("language", parameters.lang.get() ?: "")
            .add("type", parameters.type.get() ?: "")
            .build()

        val request: Request = Request.Builder()
            .url("https://api.poeditor.com/v2/projects/export")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/json")
            .post(body)
            .build()

        try {
            client.newCall(request)
                .execute().use { response ->
                if (response.code == 200) {
                    val responseExport = response.body?.string()
                    println("URL file: $responseExport")

                    val mapper = ObjectMapper()
                    val fileData: Map<String, Any> = mapper.readValue<Map<String, Any>>(responseExport,
                        object : TypeReference<Map<String, Any>?>() {})
                    val result = fileData["result"] as Map<*, *>?
                    if (result != null) {
                        val link = result["url"] as String?
                        println("INFO: $fileData")
                        val stream = URL(link).openStream()
                        Files.copy(
                            stream,
                            parameters.file.get()!!.toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                        )
                    }
                } else {
                    logger.warn("WARN: " + response.body?.string())
                }
            }
        } catch (e: IOException) {
            logger.error("Error pulling file.", e)
        }
    }
}
