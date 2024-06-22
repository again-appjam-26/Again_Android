package com.woojun.again_android.data

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_info_table")
data class AppInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val packageName: String,
    val icon: Drawable,
    var isChecked: Boolean = false
)
