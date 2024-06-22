package com.woojun.again_android.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.material.card.MaterialCardView
import com.woojun.again_android.R
import com.woojun.again_android.data.RegisterRequest
import com.woojun.again_android.data.RegisterResponse
import com.woojun.again_android.database.Preferences
import com.woojun.again_android.databinding.ActivityHobbyBinding
import com.woojun.again_android.network.RetrofitAPI
import com.woojun.again_android.network.RetrofitClient
import com.woojun.again_android.util.Dialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HobbyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHobbyBinding

    private var isGame = false
    private var isTravel = false
    private var isMovie = false
    private var isArt = false
    private var isSport = false
    private var isBook = false
    private var isMusic = false
    private var isEtc = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")
        val password = intent.getStringExtra("password")

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.gameButton.setOnClickListener {
            isGame = if (!isGame) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isGame
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isGame
            }
        }

        binding.travelButton.setOnClickListener {
            isTravel = if (!isTravel) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isTravel
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isTravel
            }
        }

        binding.movieButton.setOnClickListener {
            isMovie = if (!isMovie) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isMovie
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isMovie
            }
        }

        binding.artButton.setOnClickListener {
            isArt = if (!isArt) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isArt
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isArt
            }
        }

        binding.sportButton.setOnClickListener {
            isSport = if (!isSport) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isSport
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isSport
            }
        }

        binding.bookButton.setOnClickListener {
            isBook = if (!isBook) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isBook
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isBook
            }
        }

        binding.musicButton.setOnClickListener {
            isMusic = if (!isMusic) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isMusic
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isMusic
            }
        }

        binding.etcButton.setOnClickListener {
            isEtc = if (!isEtc) {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.yellow))
                !isEtc
            } else {
                (it as MaterialCardView).setCardBackgroundColor(resources.getColor(R.color.gray_20))
                !isEtc
            }
        }

        binding.nextButton.setOnClickListener {
            postRegister(id!!, password!!)
        }
    }

    private fun checkHobby(): List<String> {
        val categories = mutableListOf<String>()

        if (isGame) categories.add("GAME")
        if (isSport) categories.add("SPORT")
        if (isTravel) categories.add("TRAVEL")
        if (isBook) categories.add("READING")
        if (isMovie) categories.add("MOVIE")
        if (isMusic) categories.add("MUSIC")
        if (isArt) categories.add("ART")
        if (isEtc) categories.add("OTHERS")

        return categories
    }

    private fun postRegister(id: String, password: String) {
        val (loadingDialog, setDialogText) = Dialog.createLoadingDialog(this)
        setDialogText("회원가입 시도 중")
        loadingDialog.show()

        val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
        val call: Call<RegisterResponse> = retrofitAPI.postRegister(
            RegisterRequest(checkHobby(), id, password)
        )

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    Preferences.saveToken(this@HobbyActivity, response.body()!!.data.accessToken)
                    setDialogText("회원가입 완료")
                    Handler().postDelayed({
                        loadingDialog.dismiss()
                        startActivity(Intent(this@HobbyActivity, CheckActivity::class.java))
                        finishAffinity()
                    }, 500)
                } else {
                    setDialogText("회원가입 실패")
                    Handler().postDelayed({
                        loadingDialog.dismiss()
                    }, 500)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                setDialogText("회원가입 실패")
                Handler().postDelayed({
                    loadingDialog.dismiss()
                }, 500)
            }
        })
    }
}