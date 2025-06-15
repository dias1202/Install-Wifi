package com.dias.installwifi.view.user.order

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Order
import com.dias.installwifi.databinding.ActivityOrderBinding
import com.dias.installwifi.utils.FormatterUtils.formatPrice
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.user.detail.DetailPackageActivity
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.viewmodel.DetailViewModel
import com.dias.installwifi.view.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()

    private val loading by lazy { binding.loading }

    // User info
    private var userName: String = ""
    private var userId: String = ""
    private var phoneNumber: String = ""
    private var address: String = ""

    // Package info
    private var packageId: Int = 0
    private var packageName: String = ""
    private var packageSpeed: String = ""
    private var price: Double = 0.0
    private var ppn: Double = 0.0
    private var installationFee: Double = 50000.0
    private var discount: Double = 0.0
    private var totalPrice: Double = 0.0

    private var isPaymentSuccess: Boolean = false
    private var generatedOrderId: String = ""

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
                            phoneNumber = result.data.phoneNumber ?: ""
                            address = result.data.address ?: ""

                            binding.apply {
                                tvValueName.text = userName
                                tvValueAddress.text = address
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

    private fun observePackage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.getDetailPackageResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(loading, false)

                            val data = result.data
                            packageName = data.name.orEmpty()
                            packageSpeed = data.speed?.toString().orEmpty()
                            price = (data.price ?: 0.0).toDouble()
                            ppn = price * 0.11
                            totalPrice = price + ppn + installationFee - discount

                            binding.apply {
                                tvPackageName.text = data.name
                                tvValueWifi.text = data.name
                                tvValueWifiPrice.text = getString(
                                    R.string.price,
                                    formatPrice(data.price?.toInt() ?: 0)
                                )
                                tvPackageSpeed.text = getString(
                                    R.string.package_speed,
                                    data.speed.toString()
                                )
                                tvPackageType.text = getString(R.string.unlimited)
                                tvValuePpn.text =
                                    getString(R.string.price, formatPrice(ppn.toInt()))
                                tvValueInstallation.text =
                                    getString(R.string.price, formatPrice(installationFee.toInt()))
                                tvValueDiscount.text =
                                    getString(R.string.price, formatPrice(discount.toInt()))
                                tvValueTotal.text =
                                    getString(R.string.price, formatPrice(totalPrice.toInt()))
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
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.payment_confirmation))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                simulatePaymentProcess()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun simulatePaymentProcess() {
        val loadingDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.processing_payment))
            .setMessage(getString(R.string.please_wait))
            .setCancelable(false)
            .create()

        loadingDialog.show()

        lifecycleScope.launch {
            delay(3000)
            loadingDialog.dismiss()

            isPaymentSuccess = (1..100).random() > 20
            if (isPaymentSuccess) {
                createOrder()
            } else {
                navigateToPaymentResult(false, "")
            }
        }
    }

    private fun navigateToPaymentResult(isSuccess: Boolean, orderId: String) {
        val intent = Intent(this, PaymentResultActivity::class.java).apply {
            putExtra(PaymentResultActivity.EXTRA_PAYMENT_STATUS, isSuccess)
            putExtra(PaymentResultActivity.EXTRA_ORDER_ID, orderId)
            putExtra(PaymentResultActivity.EXTRA_AMOUNT, totalPrice)
        }
        startActivity(intent)
    }

    private fun createOrder() {
        orderViewModel.createOrder(
            Order(
                userId = userId,
                address = address,
                packageId = packageId.toString(),
                totalPrice = totalPrice.toInt()
            )
        )
    }

    private fun observeOrderResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.orderResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(loading, false)

                            val order = result.data
                            generatedOrderId = order.id

                            showToast(this@OrderActivity, getString(R.string.payment_success))
                            navigateToPaymentResult(true, generatedOrderId)
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