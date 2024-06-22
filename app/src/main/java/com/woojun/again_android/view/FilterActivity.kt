package com.woojun.again_android.view

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.again_android.MainActivity
import com.woojun.again_android.adapter.AppAdapter
import com.woojun.again_android.data.AppInfo
import com.woojun.again_android.database.AppDatabase
import com.woojun.again_android.database.Preferences
import com.woojun.again_android.database.Preferences.saveAppTime
import com.woojun.again_android.database.Preferences.saveDate
import com.woojun.again_android.database.Preferences.saveTime
import com.woojun.again_android.databinding.ActivityFilterBinding
import com.woojun.again_android.service.AppBlockAccessibilityService
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


        startActivityForResult(Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS), 1)


        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityServiceDialog()
        }

        val checkedList = intent.getStringArrayListExtra("checkedList")
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
                    saveAppTime(this@FilterActivity, 0)

                    startActivity(Intent(this@FilterActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = ComponentName(this, AppBlockAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        if (enabledServices.isNullOrEmpty()) {
            return false
        }
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)

        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponent != null && enabledComponent == expectedComponentName) {
                return true
            }
        }

        return false
    }

    private fun showAccessibilityServiceDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("앱을 사용하려면 접근성 서비스 권한이 필요합니다.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
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
        Log.d("확인1", "$usageStatsList")

        for (usageStats in usageStatsList) {
            val packageName = usageStats.packageName
            if (packageNames.contains(packageName)) {
                val totalTimeInForeground = usageStats.totalTimeInForeground
                val totalTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes(totalTimeInForeground)
                Log.d("확인", "$time, $totalTimeInMinutes")
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

            AppInfo(name = appName, packageName = packageName)
        }
    }

    private fun filterAppInfoByName(appInfoList: List<AppInfo>, namesToCompare: List<String>): List<AppInfo> {
        val nameSet = namesToCompare.toSet()

        return appInfoList.filter { appInfo ->
            appInfo.name in nameSet
        }
    }
}