package com.dias.installwifi.view.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Package
import com.dias.installwifi.databinding.ActivityDetailPackageBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPackageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPackageBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val packageId = intent.getIntExtra(EXTRA_PACKAGE_ID, 0)
        viewModel.getPackageById(packageId)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getDetailPackageResult.collectLatest { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(binding.loading, false)
                            displayPackageDetails(result.data)
                        }

                        is ResultState.Error -> {
                            showLoading(binding.loading, false)
                            showToast(this@DetailPackageActivity, result.error)
                        }

                        is ResultState.Loading -> {
                            showLoading(binding.loading, true)
                        }
                    }
                }
            }
        }
    }

    private fun displayPackageDetails(packageData: Package) {
        binding.apply {
            tvPackageName.text = packageData.name
            tvPackageDescription.text = packageData.description
            tvPackageTerms.text = packageData.termsAndConditions
            tvPackageSpeed.text = packageData.speed.toString()
            tvPackagePrice.text = packageData.price.toString()

            Glide.with(ivPackage.context)
                .load(packageData.imageHorizontalUrl)
                .into(ivPackage)
        }
    }

    companion object {
        const val EXTRA_PACKAGE_ID = "package_data_id"
    }
}
