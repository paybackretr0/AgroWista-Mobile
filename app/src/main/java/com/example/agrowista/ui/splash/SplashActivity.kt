package com.example.agrowista.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.agrowista.MainActivity
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.databinding.ActivitySplashBinding
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userPreferences = UserPreferences.getInstance(this)

        lifecycleScope.launch {
            userPreferences.isUserLoggedIn().collect { isLoggedIn ->
                val intent = if (isLoggedIn) {
                    Intent(this@SplashActivity, MainActivity::class.java)
                } else {
                    Intent(this@SplashActivity, MainActivity::class.java).apply {
                        putExtra("should_navigate_login", true)
                    }
                }
                startActivity(intent)
                finish()
            }
        }
    }
}