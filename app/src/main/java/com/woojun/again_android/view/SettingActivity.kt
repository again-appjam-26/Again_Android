package com.woojun.again_android.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.woojun.again_android.database.Preferences.loadTime
import com.woojun.again_android.database.Preferences.loadToken
import com.woojun.again_android.database.Preferences.resetToken
import com.woojun.again_android.databinding.ActivitySettingBinding
import com.woojun.again_android.network.RetrofitAPI
import com.woojun.again_android.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            val time = loadTime(this@SettingActivity)
            timeText.text = "${time/60}시간 ${time%60}분"

            backButton.setOnClickListener {
                finish()
            }

            logoutButton.setOnClickListener {
                resetToken(this@SettingActivity)
                startActivity(Intent(this@SettingActivity, StartActivity::class.java))
                finishAffinity()
            }

            signoutButton.setOnClickListener {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val call: Call<Void> = retrofitAPI.deleteUser(loadToken(this@SettingActivity)!!)

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {
                        if (response.isSuccessful) {
                            resetToken(this@SettingActivity)
                            startActivity(Intent(this@SettingActivity, StartActivity::class.java))
                            finishAffinity()
                        } else {
                            Toast.makeText(this@SettingActivity, "에러", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@SettingActivity, "에러", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}