package com.woojun.again_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woojun.again_android.R
import com.woojun.again_android.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}