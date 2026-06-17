package com.example.kalku.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.MainActivity
import com.example.kalku.databinding.ActivitySplashBinding
import com.example.kalku.login.LoginActivity
import com.example.kalku.onboarding.OnboardingActivity
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(1400)

            val onboardingDone = getSharedPreferences(
                OnboardingActivity.PREF_NAME,
                MODE_PRIVATE
            ).getBoolean(OnboardingActivity.KEY_ONBOARDING_DONE, false)

            val destination = when {
                !onboardingDone -> OnboardingActivity::class.java
                SessionManager(this@SplashActivity).isLoggedIn() -> MainActivity::class.java
                else -> LoginActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, destination))
            finish()
        }
    }
}
