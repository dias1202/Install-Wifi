package com.dias.installwifi.view.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dias.installwifi.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
    }
}