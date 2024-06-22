package com.woojun.again_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woojun.again_android.R
import com.woojun.again_android.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}