package com.woojun.again_android.network

import com.woojun.again_android.BuildConfig
import com.woojun.again_android.data.CheckAppResponse
import com.woojun.again_android.data.CheckAppsRequest
import com.woojun.again_android.data.InterestResponse
import com.woojun.again_android.data.LoginRequest
import com.woojun.again_android.data.LoginResponse
import com.woojun.again_android.data.RegisterRequest
import com.woojun.again_android.data.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST


interface RetrofitAPI {
    @POST("${BuildConfig.baseUrl}member/login")
    fun postLogin(
        @Body body: LoginRequest
    ): Call<LoginResponse>

    @POST("${BuildConfig.baseUrl}member/register")
    fun postRegister(
        @Body body: RegisterRequest
    ): Call<RegisterResponse>

    @POST("${BuildConfig.baseUrl}check_apps")
    suspend fun postCheckApp(
        @Body body: CheckAppsRequest
    ): Response<CheckAppResponse>

    @DELETE
    fun deleteUser(@Header("Authorization") authorization: String): Call<Void>

    @POST("${BuildConfig.baseUrl}interest")
    suspend fun postInterest(@Header("Authorization") authorization: String): Response<InterestResponse>
}