package com.woojun.again_android.view

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.woojun.again_android.R
import com.woojun.again_android.data.AppInfo
import com.woojun.again_android.data.CheckAppsRequest
import com.woojun.again_android.databinding.ActivityCheckBinding
import com.woojun.again_android.network.RetrofitAPI
import com.woojun.again_android.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckBinding
    private var checkedList: ArrayList<String>? = null
    private var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            backButton.setOnClickListener {
                finish()
            }

            nextButton.setOnClickListener {
                if (!isChecked) {
                    it.visibility = View.GONE
                    CoroutineScope(Dispatchers.IO).launch {
                        checkedList = postCheckApp()
                    }
                } else {
                    if (checkedList != null) {
                        startActivity(
                            Intent(this@CheckActivity, FilterActivity::class.java).putExtra(
                                "checkedList",
                                checkedList!!
                            )
                        )
                    }
                }
            }

        }
    }

    private suspend fun postCheckApp(): ArrayList<String>? {

        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.postCheckApp(
                    CheckAppsRequest(getAppInfo().map { it.name })
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.animationView.setAnimation(R.raw.check)
                    }
                    val list = arrayListOf<String>()
                    list.addAll(response.body()!!.data)
                    list
                } else {
                    Toast.makeText(this@CheckActivity, "스캔을 실패 했습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@CheckActivity, "스캔을 실패 했습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            null
        }
    }



    private fun getAppInfo(): List<AppInfo> {
        val packageManager: PackageManager = packageManager
        val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

        return packages.map {
            val appInfo = it.applicationInfo

            val appName = appInfo.loadLabel(packageManager).toString()
            val packageName = it.packageName
            val appIcon = appInfo.loadIcon(packageManager)

            AppInfo(name = appName, packageName = packageName, icon = appIcon)
        }

    }
}