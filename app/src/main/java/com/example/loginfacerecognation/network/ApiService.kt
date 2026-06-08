package com.example.loginfacerecognation.network

import com.example.loginfacerecognation.model.LoginRequest
import com.example.loginfacerecognation.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiService(
    private val baseUrl: String = BASE_URL
) {
    suspend fun submitLogin(request: LoginRequest): LoginResponse = withContext(Dispatchers.IO) {
        val connection = (URL("${baseUrl}FaceLogin").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val payload = JSONObject()
                .put("userId", request.userId)
                .put("faceConfidence", request.faceConfidence.toDouble())
                .put("deviceId", request.deviceId)
                .toString()

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(payload)
            }

            val stream = if (connection.responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val body = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
                reader.readText()
            }
            val json = JSONObject(body)

            LoginResponse(
                success = json.optBoolean("success", false),
                token = json.optString("token").takeIf { it.isNotBlank() && it != "null" },
                userName = json.optString("userName").takeIf { it.isNotBlank() && it != "null" },
                message = json.optString("message").takeIf { it.isNotBlank() && it != "null" }
            )
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        // Azure-hosted AJInvestment backend.
        const val BASE_URL = "http://20.164.210.147:8080/AJInvestment/"
    }
}
