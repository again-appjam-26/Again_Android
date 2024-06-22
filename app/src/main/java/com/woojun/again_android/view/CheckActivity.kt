package com.woojun.again_android.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private val REQUEST_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            if (!checkPermissions()) {
                requestPermissions()
            } else {
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

            backButton.setOnClickListener {
                finish()
            }

        }
    }

    private fun checkPermissions(): Boolean {
        val queryAllPackagesPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES)
        val packageUsageStatsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS)

        return queryAllPackagesPermission == PackageManager.PERMISSION_GRANTED &&
                packageUsageStatsPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.QUERY_ALL_PACKAGES, Manifest.permission.PACKAGE_USAGE_STATS),
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                binding.nextButton.setOnClickListener {
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
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("앱을 사용하기 위해 필요한 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
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