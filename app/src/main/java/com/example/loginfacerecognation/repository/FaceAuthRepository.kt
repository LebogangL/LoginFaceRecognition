package com.example.loginfacerecognation.repository

import android.util.Log
import com.example.loginfacerecognation.model.LoginRequest
import com.example.loginfacerecognation.model.LoginResponse
import com.example.loginfacerecognation.network.ApiService

class FaceAuthRepository(
    private val apiService: ApiService = ApiService()
) {
    suspend fun login(userId: String, faceConfidence: Float, deviceId: String): LoginResponse? {
        return try {
            apiService.submitLogin(
                LoginRequest(
                    userId = userId,
                    faceConfidence = faceConfidence,
                    deviceId = deviceId
                )
            )
        } catch (e: Exception) {
            Log.e("FaceAuthRepository", "Backend face login failed", e)
            null
        }
    }
}
