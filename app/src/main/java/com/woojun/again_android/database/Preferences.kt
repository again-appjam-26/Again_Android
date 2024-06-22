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

    fun resetToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("accessToken", null)
        editor.apply()
    }

    fun saveTime(context: Context, time: Long) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong("time", time)
        editor.apply()
    }

    fun loadTime(context: Context): Long {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("time", 0)
    }

    fun saveDate(context: Context, date: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("date", date)
        editor.apply()
    }

    fun loadDate(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("date", null)
    }

    fun saveAppTime(context: Context, time: Long) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong("app_time", time)
        editor.apply()
    }

    fun loadAppTime(context: Context): Long {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("app_time", 0)
    }

    fun saveFirstDate(context: Context, date: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("first_date", date)
        editor.apply()
    }

    fun loadFirstDate(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("first_date", null)
    }

}