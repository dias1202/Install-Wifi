package com.dias.installwifi.view.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dias.installwifi.view.authentication.login.LoginFragment
import com.dias.installwifi.databinding.ActivityAuthBinding
import com.dias.installwifi.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, loginFragment)
            .commit()
    }
}