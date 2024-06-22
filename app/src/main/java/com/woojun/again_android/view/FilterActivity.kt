package com.woojun.again_android.view

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.again_android.MainActivity
import com.woojun.again_android.adapter.AppAdapter
import com.woojun.again_android.data.AppInfo
import com.woojun.again_android.database.AppDatabase
import com.woojun.again_android.databinding.ActivityFilterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val checkedList = intent.getStringArrayExtra("checkedList")
        val filterList = filterAppInfoByName(getAppInfo(), checkedList!!.toList())

        val mainAdapter = AppAdapter(filterList.toMutableList())

        binding.filterList.adapter = mainAdapter
        binding.filterList.layoutManager = LinearLayoutManager(this@FilterActivity)

        binding.nextButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val appInfoDAO = AppDatabase.getDatabase(this@FilterActivity)?.appInfoDAO()
                appInfoDAO?.insertAppInfoList(mainAdapter.getCheckedAppList())

                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@FilterActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }

        binding.nextButton.setOnClickListener {
            finish()
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

    private fun filterAppInfoByName(appInfoList: List<AppInfo>, namesToCompare: List<String>): List<AppInfo> {
        val nameSet = namesToCompare.toSet()

        return appInfoList.filter { appInfo ->
            appInfo.name in nameSet
        }
    }
}