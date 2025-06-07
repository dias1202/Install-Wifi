package com.dias.installwifi.view.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Package
import com.dias.installwifi.databinding.ActivityDetailPackageBinding
import com.dias.installwifi.utils.FormatPrice.formatPrice
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.order.OrderActivity
import com.dias.installwifi.view.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.jvm.java

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

        binding.apply {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnOrder.setOnClickListener {
                navigateToOrderScreen(packageId)
            }
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
            tvPackageSpeed.text = tvPackageSpeed.context.getString(
                R.string.package_speed,
                packageData.speed.toString()
            )
            tvPackagePrice.text =
                tvPackagePrice.context.getString(R.string.price, formatPrice(packageData.price?.toInt()
                    ?: 0))

            Glide.with(ivPackage.context)
                .load(packageData.imageHorizontalUrl)
                .into(ivPackage)
        }
    }

    private fun navigateToOrderScreen(id: Int? = 0) {
        val intent = Intent(this, OrderActivity::class.java).apply {
            putExtra(EXTRA_PACKAGE_ID, id)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_PACKAGE_ID = "package_data_id"
    }
}
