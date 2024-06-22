package com.woojun.again_android.view

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.again_android.MainActivity
import com.woojun.again_android.adapter.AppAdapter
import com.woojun.again_android.data.AppInfo
import com.woojun.again_android.database.AppDatabase
import com.woojun.again_android.database.Preferences.saveDate
import com.woojun.again_android.database.Preferences.saveTime
import com.woojun.again_android.databinding.ActivityFilterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

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
            val item = mainAdapter.getCheckedAppList()

            CoroutineScope(Dispatchers.IO).launch {
                val appInfoDAO = AppDatabase.getDatabase(this@FilterActivity)?.appInfoDAO()
                appInfoDAO?.insertAppInfoList(item)

                withContext(Dispatchers.Main) {
                    saveTime(this@FilterActivity, timeCalculation(this@FilterActivity, item.map { it.packageName }))
                    saveDate(this@FilterActivity, getDate())

                    startActivity(Intent(this@FilterActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }

        binding.nextButton.setOnClickListener {
            finish()
        }
    }

    private fun getDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        return currentDate.format(formatter)
    }

    private fun timeCalculation(context: Context, packageNames: List<String>): Long {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.HOURS.toMillis(24)

        val usageStatsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        var time: Long = 0

        for (usageStats in usageStatsList) {
            val packageName = usageStats.packageName
            if (packageNames.contains(packageName)) {
                val totalTimeInForeground = usageStats.totalTimeInForeground
                val totalTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes(totalTimeInForeground)
                time+=totalTimeInMinutes
            }
        }

        return time
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