package com.woojun.again_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woojun.again_android.R
import com.woojun.again_android.databinding.ActivityCheckBinding

class CheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}