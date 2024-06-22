package com.woojun.again_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woojun.again_android.R
import com.woojun.again_android.databinding.ActivityTimeOverBinding

class TimeOverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeOverBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeOverBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}