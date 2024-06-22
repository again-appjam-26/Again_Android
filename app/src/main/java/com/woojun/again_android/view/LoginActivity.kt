package com.woojun.again_android.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.woojun.again_android.MainActivity
import com.woojun.again_android.R
import com.woojun.again_android.data.LoginRequest
import com.woojun.again_android.data.LoginResponse
import com.woojun.again_android.database.Preferences.saveToken
import com.woojun.again_android.databinding.ActivityLoginBinding
import com.woojun.again_android.network.RetrofitAPI
import com.woojun.again_android.network.RetrofitClient
import com.woojun.again_android.util.Dialog.createLoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var passwordToggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

            finishButton.setOnClickListener {
                postLogin(binding.idinput.text.toString(), binding.passwordInput.text.toString())
            }
        }
    }

    private fun postLogin(id: String, password: String) {
        val (loadingDialog, setDialogText) = createLoadingDialog(this)
        setDialogText("로그인 시도 중")
        loadingDialog.show()

        val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
        val call: Call<LoginResponse> = retrofitAPI.postLogin(LoginRequest(id, password))

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    saveToken(this@LoginActivity, response.body()!!.data.accessToken)
                    setDialogText("로그인 완료")
                    Handler().postDelayed({
                        loadingDialog.dismiss()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }, 500)
                } else {
                    setDialogText("로그인 실패")
                    Handler().postDelayed({
                        loadingDialog.dismiss()
                    }, 500)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                setDialogText("로그인 실패")
                Log.d("확인", t.toString())
                Handler().postDelayed({
                    loadingDialog.dismiss()
                }, 500)
            }
        })
    }
}