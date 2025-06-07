package com.dias.installwifi.view.order

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Order
import com.dias.installwifi.databinding.ActivityOrderBinding
import com.dias.installwifi.utils.FormatPrice.formatPrice
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.detail.DetailPackageActivity
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.viewmodel.DetailViewModel
import com.dias.installwifi.view.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()

    private val loading by lazy { binding.loading }

    private var userName: String = ""
    private var userId: String = ""
    private var packageId: Int = 0
    private var packageName: String = ""
    private var packageSpeed: String = ""
    private var price: Double = 0.0
    private var ppn: Double = 0.0
    private var installationFee: Double = 50000.0
    private var discount: Double = 0.0
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        packageId = intent.getIntExtra(DetailPackageActivity.EXTRA_PACKAGE_ID, 0)

        authViewModel.getSession()
        detailViewModel.getPackageById(packageId)

        observeSession()
        observePackage()
        observeOrderResult()

        binding.btnPay.setOnClickListener { submitOrder() }
    }

    private fun observeSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            userName = result.data.name
                            userId = result.data.uid
                            binding.tvValueName.text = userName
                        }

                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(this@OrderActivity, result.error)
                        }

                        ResultState.Loading -> showLoading(loading, true)
                    }
                }
            }
        }
    }

    private fun observePackage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.getDetailPackageResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            packageName = result.data.name ?: ""
                            packageSpeed = result.data.speed?.toString() ?: ""
                            price = (result.data.price ?: 0.0).toDouble()
                            ppn = price * 0.11
                            totalPrice = price + ppn + installationFee - discount

                            binding.apply {
                                tvPackageName.text = result.data.name
                                tvValueWifi.text = result.data.name
                                tvValueWifiPrice.text = tvValueWifiPrice.context.getString(
                                    R.string.price, formatPrice(
                                        result.data.price?.toInt() ?: 0
                                    )
                                )
                                tvPackageSpeed.text = tvPackageSpeed.context.getString(
                                    R.string.package_speed,
                                    result.data.speed.toString()
                                )
                                tvPackageType.text =
                                    tvPackageType.context.getString(R.string.unlimited)
                                tvValuePpn.text =
                                    tvValuePpn.context.getString(
                                        R.string.price, formatPrice(
                                            ppn.toInt()
                                        )
                                    )
                                tvValueInstallation.text = tvValueInstallation.context.getString(
                                    R.string.price,
                                    formatPrice(installationFee.toInt())
                                )
                                tvValueDiscount.text = tvValueDiscount.context.getString(
                                    R.string.price,
                                    formatPrice(discount.toInt())
                                )
                                tvValueTotal.text = tvValueTotal.context.getString(
                                    R.string.price,
                                    formatPrice(totalPrice.toInt())
                                )
                            }
                        }

                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(this@OrderActivity, result.error)
                        }

                        ResultState.Loading -> showLoading(loading, true)
                    }
                }
            }
        }
    }

    private fun submitOrder() {
        val order = Order(
            userId = userId,
            userName = userName,
            packageId = packageId,
            packageName = packageName,
            packageSpeed = packageSpeed,
            price = price,
            ppn = ppn,
            installationFee = installationFee,
            discount = discount,
            totalPrice = totalPrice
        )
        orderViewModel.postOrder(order)
    }

    private fun observeOrderResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.orderResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            showToast(this@OrderActivity, "Order successful! ID: ${result.data}")
                            finish()
                        }
                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(this@OrderActivity, result.error)
                        }
                        ResultState.Loading -> showLoading(loading, true)
                    }
                }
            }
        }
    }
}

