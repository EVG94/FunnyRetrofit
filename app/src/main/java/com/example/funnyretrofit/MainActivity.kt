package com.example.funnyretrofit

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.funnyretrofit.api.ApiRequest
import com.example.funnyretrofit.api.BASE_URL
import com.example.funnyretrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


import java.lang.Exception


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keeps the phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Starts up the background transitions
        startBackgroundAnimation()

        // Makes an API request as soon as the app starts

            makeRequest()


        binding.floatingActionButton.setOnClickListener {

            // FAB rotate animation
            binding.floatingActionButton.animate().apply {
                rotationBy(360f)
                duration = 1000
            }.start()

            makeRequest()
            binding.ivRandomDog.visibility = View.GONE

        }
    }

    private fun startBackgroundAnimation() {
        val anim: AnimationDrawable = binding.rlLayout.background as AnimationDrawable
        anim.apply {
            setEnterFadeDuration(1000)
            setExitFadeDuration(3000)
            start()
        }
    }

    private fun makeRequest() {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getRandomDog()
                Log.d("Main", "Size: ${response.fileSizeBytes}")

                //If the image is less than about 0.4mb, then we try to load it into our app, else we try again.
                if (response.fileSizeBytes < 400_000) {
                    withContext(Dispatchers.Main) {
                        Glide.with(applicationContext).load(response.url).into(binding.ivRandomDog)
                        binding.ivRandomDog.visibility = View.VISIBLE
                    }
                } else {
                    makeRequest()
                }

            } catch (e: Exception) {
                Log.e("errorTag", "Error: ${e.message}")
            }
        }
    }

}