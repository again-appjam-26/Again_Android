package com.woojun.again_android

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.again_android.adapter.SuggestAdapter
import com.woojun.again_android.adapter.TimeAdapter
import com.woojun.again_android.data.AppTimeInfo
import com.woojun.again_android.data.Suggest
import com.woojun.again_android.database.Preferences.loadDate
import com.woojun.again_android.database.Preferences.loadTime
import com.woojun.again_android.database.Preferences.loadToken
import com.woojun.again_android.database.Preferences.saveAppTime
import com.woojun.again_android.database.Preferences.saveDate
import com.woojun.again_android.database.Preferences.saveTime
import com.woojun.again_android.databinding.ActivityMainBinding
import com.woojun.again_android.network.RetrofitAPI
import com.woojun.again_android.network.RetrofitClient
import com.woojun.again_android.service.AppBlockAccessibilityService
import com.woojun.again_android.view.SettingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            settingButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            }

            if (loadDate(this@MainActivity) != getDate()) {
                val yesterdayTime = loadTime(this@MainActivity)
                val nowTime = yesterdayTime - (yesterdayTime/20)

                saveDate(this@MainActivity, getDate())

                if (nowTime > 120) {
                    saveTime(this@MainActivity, nowTime)
                }

                saveAppTime(this@MainActivity, 0)
            }

            Log.d("확인", loadTime(this@MainActivity).toString())

            val time = loadTime(this@MainActivity)

            binding.nowTime.text = "오늘은 도파민 사용 시간이\n${time/60}시간 ${time%60}분 남았어요!"

            val tomorrowTime = time - (time/20)

            textView5.text = "내일의 도파민 할당 시간 : ${tomorrowTime/60}시간 ${tomorrowTime%60}분"

            binding.appList.adapter = TimeAdapter(printRecentAppsUsageInfo(this@MainActivity).toMutableList())
            binding.appList.layoutManager = LinearLayoutManager(this@MainActivity)

            CoroutineScope(Dispatchers.IO).launch {
                sleep(3300)
                val list = postInterest()
                Log.d("확인2", list.toString())
                if (list != null) {
                    binding.suggestList.adapter = SuggestAdapter(list.toMutableList())
                    binding.suggestList.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }
        }
    }

    private suspend fun postInterest(): List<Suggest>? {
        Log.d("확인1", loadToken(this@MainActivity)!!)

        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.postInterest(loadToken(this@MainActivity)!!)
                if (response.isSuccessful) {
                    response.body()!!.data.map {
                        Suggest(it.name, it.image, it.url)
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.d("확인", e.toString())
            null
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getRecentAppsUsageInfo(context: Context): List<AppTimeInfo> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val packageManager = context.packageManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 60 * 60 * 1000) // 지난 24시간

        val usageStatsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        val appUsageInfoList = mutableListOf<AppTimeInfo>()

        for (usageStats in usageStatsList) {
            val packageName = usageStats.packageName
            val totalTimeInForeground = usageStats.totalTimeInForeground

            if (totalTimeInForeground > 0) {
                try {
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    appUsageInfoList.add(AppTimeInfo(appName, totalTimeInForeground.toString()))
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

        return appUsageInfoList
    }

    private fun printRecentAppsUsageInfo(context: Context): List<AppTimeInfo>  {
        val list = mutableListOf<AppTimeInfo>()
        val appUsageInfoList = getRecentAppsUsageInfo(context)

        for (appUsageInfo in appUsageInfoList) {
            val hours = appUsageInfo.time.toLong() / (1000 * 60 * 60)
            val minutes = (appUsageInfo.time.toLong() / (1000 * 60)) % 60

            list.add(AppTimeInfo(appUsageInfo.name, "${hours}시 ${minutes}분"))
        }

        return list
    }
}