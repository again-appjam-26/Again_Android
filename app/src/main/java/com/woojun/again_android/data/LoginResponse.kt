package com.woojun.again_android.data

data class LoginResponse(
    val data: Data,
    val message: String,
    val status: Int
)

data class Data(
    val accessToken: String,
    val refreshToken: String
)