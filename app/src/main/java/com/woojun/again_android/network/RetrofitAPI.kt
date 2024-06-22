package com.woojun.again_android.network

import com.woojun.again_android.BuildConfig
import com.woojun.again_android.data.LoginRequest
import com.woojun.again_android.data.LoginResponse
import com.woojun.again_android.data.RegisterRequest
import com.woojun.again_android.data.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part


interface RetrofitAPI {
    @POST("${BuildConfig.baseUrl}member/login")
    fun postLogin(
        @Body body: LoginRequest
    ): Call<LoginResponse>

    @POST("${BuildConfig.baseUrl}member/register")
    fun postRegister(
        @Body body: RegisterRequest
    ): Call<RegisterResponse>

}