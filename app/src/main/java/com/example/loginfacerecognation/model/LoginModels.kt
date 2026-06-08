package com.example.loginfacerecognation.model

data class LoginRequest(
    val userId: String,
    val faceConfidence: Float,
    val deviceId: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val userName: String?,
    val message: String?
)
