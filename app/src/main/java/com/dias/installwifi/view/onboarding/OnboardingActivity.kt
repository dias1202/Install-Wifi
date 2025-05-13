package com.dias.installwifi.view.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.dias.installwifi.databinding.ActivityOnboardingBinding
import com.dias.installwifi.view.authentication.AuthActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            tvSkip.setOnClickListener {
                moveToAuth()
            }
            tvLogin.setOnClickListener {
                moveToAuth()
            }
        }

    }

    private fun moveToAuth() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean("isFirstLaunch", false)
        }

        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}