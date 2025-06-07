package com.dias.installwifi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.ActivitySplashScreenBinding
import com.dias.installwifi.view.authentication.AuthActivity
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.content.edit

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2000)
    }

    private fun checkUserStatus() {
        val sharedPreferences = getSharedPreferences("appPreferences", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            sharedPreferences.edit { putBoolean("isFirstLaunch", false) }
            navigateToOnboardingActivity()
            return
        }

        authViewModel.getSession()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect {
                    when (it) {
                        ResultState.Loading -> false
                        is ResultState.Success -> {
                            if (it.data.isLogin == true || it.data.isGoogleLogin == true) {
                                navigateToMainActivity()
                            } else {
                                navigateToAuthActivity()
                            }
                        }

                        is ResultState.Error -> navigateToAuthActivity()
                    }
                }
            }
        }
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToOnboardingActivity() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}