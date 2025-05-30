package com.dias.installwifi.view.info

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dias.installwifi.R
import com.dias.installwifi.databinding.ActivityInfoBinding
import com.dias.installwifi.view.adapter.TabAdapter
import com.google.android.material.tabs.TabLayoutMediator

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val tabAdapter = TabAdapter(this)
        viewPager.adapter = tabAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.notification)
                1 -> tab.text = getString(R.string.news)
            }
        }.attach()
    }
}