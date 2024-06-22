package com.woojun.again_android.view

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.again_android.MainActivity
import com.woojun.again_android.adapter.SuggestAdapter
import com.woojun.again_android.data.Suggest
import com.woojun.again_android.database.Preferences
import com.woojun.again_android.databinding.ActivityTimeOverBinding
import com.woojun.again_android.network.RetrofitAPI
import com.woojun.again_android.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimeOverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeOverBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeOverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            goButton.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            val originalPackage = intent.getStringExtra("original_package")

            goButton.setOnClickListener {
                originalPackage?.let { packageName ->
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                    launchIntent?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                }
            }

            finishButton.setOnClickListener {
                startActivity(Intent(this@TimeOverActivity, MainActivity::class.java))
                finishAffinity()
            }

            CoroutineScope(Dispatchers.IO).launch {
                val list = postInterest()
                if (list != null) {
                    binding.suggestList.adapter = SuggestAdapter(list.toMutableList())
                    binding.suggestList.layoutManager = LinearLayoutManager(this@TimeOverActivity)
                }
            }
        }
    }

    private suspend fun postInterest(): List<Suggest>? {

        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.postInterest(Preferences.loadToken(this@TimeOverActivity)!!)
                if (response.isSuccessful) {
                    response.body()!!.data.map {
                        Suggest(it.name, it.image, it.url)
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

}