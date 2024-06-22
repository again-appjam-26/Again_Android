package com.woojun.again_android.database

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("accessToken", token)
        editor.apply()
    }

    fun loadToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("accessToken", null)
    }

}