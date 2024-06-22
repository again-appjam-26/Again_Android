package com.woojun.again_android.data

data class RegisterRequest(
    val interest: List<Any>,
    val password: String,
    val userId: String
)