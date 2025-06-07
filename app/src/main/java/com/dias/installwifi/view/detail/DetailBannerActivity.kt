package com.dias.installwifi.view.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Banner
import com.dias.installwifi.databinding.ActivityDetailBannerBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailBannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBannerBinding

    private val viewModel: DetailViewModel by viewModels()

    private val bannerId: Int by lazy {
        intent.getIntExtra(EXTRA_BANNER_ID, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getBannerPromoById(bannerId)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        observerDetailBannerPromo()
    }

    private fun observerDetailBannerPromo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getDetailBannerPromoResult.collectLatest { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(binding.loading, false)
                            displayPackageDetails(result.data)
                        }

                        is ResultState.Error -> {
                            showLoading(binding.loading, false)
                            showToast(this@DetailBannerActivity, result.error)
                        }

                        is ResultState.Loading -> {
                            showLoading(binding.loading, true)
                        }
                    }
                }
            }
        }
    }

    private fun displayPackageDetails(banner: Banner) {
        binding.apply {
            Glide.with(ivBanner.context)
                .load(banner.imageUrl)
                .into(ivBanner)

            tvTermAndCondition.text = banner.termsAndConditions
        }
    }

    companion object {
        const val EXTRA_BANNER_ID = "extra_banner_id"
    }
}