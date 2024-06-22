package com.woojun.again_android.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.woojun.again_android.database.AppDatabase
import com.woojun.again_android.database.Preferences.loadAppTime
import com.woojun.again_android.database.Preferences.loadTime
import com.woojun.again_android.database.Preferences.saveAppTime
import com.woojun.again_android.view.TimeOverActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppBlockAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString()

        CoroutineScope(Dispatchers.IO).launch {
            if (packageName != null) {
                val appInfoDAO = AppDatabase.getDatabase(baseContext)?.appInfoDAO()
                val packageList = appInfoDAO?.getAppInfoList()?.map { it.packageName }

                if (packageList != null) {
                    if (packageName in packageList) {
                        trackAppUsage(packageName)
                    }
                }

            }

        }


    }

    override fun onInterrupt() {

    }

    private fun blockApp(originalPackage: String) {
        val intent = Intent(baseContext, TimeOverActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("original_package", originalPackage)
        startActivity(intent)
    }

    private fun trackAppUsage(originalPackage: String) {
        val currentTime = System.currentTimeMillis()
        val lastUsedTime = loadAppTime(baseContext)
        val usageTime = currentTime + lastUsedTime

        if (lastUsedTime != 0L) {

            if (usageTime > loadTime(baseContext)) {
                blockApp(originalPackage)
            }
        }

        saveAppTime(baseContext, (usageTime+currentTime))
    }

}
