package com.woojun.again_android.database

import androidx.room.*
import com.woojun.again_android.data.AppInfo

@Dao
interface AppInfoDAO {
    @Insert
    fun insertAppInfoList(appInfo: List<AppInfo>)

    @Query("SELECT * FROM app_info_table")
    fun getAppInfoList(): MutableList<AppInfo>

    @Delete
    fun deleteAppInfoList(appInfo: AppInfo)
}