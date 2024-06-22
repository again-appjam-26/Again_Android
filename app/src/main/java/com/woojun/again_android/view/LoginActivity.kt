package com.woojun.again_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woojun.again_android.R
import com.woojun.again_android.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}