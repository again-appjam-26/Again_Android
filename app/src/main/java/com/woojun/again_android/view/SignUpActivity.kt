package com.woojun.again_android.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import com.woojun.again_android.R
import com.woojun.again_android.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var passwordToggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            backButton.setOnClickListener {
                finish()
            }

            passwordVisibleButton.setOnClickListener {
                if (!passwordToggle) {
                    passwordInput.inputType = InputType.TYPE_CLASS_TEXT
                    passwordVisibleButton.setImageResource(R.drawable.visible_icon)
                    passwordToggle = !passwordToggle
                    passwordInput.setSelection(passwordInput.text.length)
                } else {
                    passwordVisibleButton.setImageResource(R.drawable.invisible_icon)
                    passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordToggle = !passwordToggle
                    passwordInput.setSelection(passwordInput.text.length)
                }
            }

            nextButton.setOnClickListener {
                startActivity(
                    Intent(this@SignUpActivity, HobbyActivity::class.java).apply {
                        putExtra("id", binding.idinput.text.toString())
                        putExtra("password", binding.passwordInput.text.toString())
                    }
                )
            }
        }

    }
}