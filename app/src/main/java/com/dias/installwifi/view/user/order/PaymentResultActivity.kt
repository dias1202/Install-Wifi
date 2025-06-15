package com.dias.installwifi.view.user.order

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dias.installwifi.R
import com.dias.installwifi.databinding.ActivityPaymentResultBinding
import com.dias.installwifi.utils.FormatterUtils.formatPrice
import com.dias.installwifi.view.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dias.installwifi.MainActivity
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import kotlinx.coroutines.launch
import kotlin.jvm.java

@AndroidEntryPoint
class PaymentResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentResultBinding

    private val orderViewModel: OrderViewModel by viewModels()

    private val loading by lazy { binding.loading }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isSuccess = intent.getBooleanExtra(EXTRA_PAYMENT_STATUS, false)
        val orderId = intent.getStringExtra(EXTRA_ORDER_ID)
        val amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0)

        if (isSuccess) {
            paymentSuccess(orderId, amount)
        } else {
            paymentFailed(amount)
        }

        binding.apply {
            btnCaptureProof.setOnClickListener {
                val screenshot = captureScreen()
                orderViewModel.uploadPaymentProof(orderId.toString(), screenshot)
                observeUploadPaymentProofResult()
            }
            btnFinish.setOnClickListener {
                if (isSuccess) {
                    finish()
                } else {
                    navigateBack()
                }
            }
        }
    }

    private fun paymentSuccess(orderId: String?, amount: Double) {
        binding.apply {
            imgPaymentStatus.setImageResource(R.drawable.payment_success)
            tvPaymentStatus.text = getString(R.string.payment_success)
            tvPaymentDescription.text =
                getString(
                    R.string.payment_of_successfully_processed,
                    formatPrice(amount.toInt())
                )
            tvOrderId.text = getString(R.string.order_id, orderId)
            btnFinish.text = getString(R.string.finish)
        }
    }

    private fun paymentFailed(amount: Double) {
        binding.apply {
            imgPaymentStatus.setImageResource(R.drawable.payment_failed)
            tvPaymentStatus.text = getString(R.string.payment_failed)
            tvPaymentDescription.text =
                getString(
                    R.string.payment_of_failed_to_be_processed,
                    formatPrice(amount.toInt())
                )
            btnFinish.text = getString(R.string.try_again)
            btnCaptureProof.visibility = View.GONE
            tvNote.visibility = View.GONE
        }
    }

    private fun captureScreen(): Bitmap {
        val view = window.decorView.rootView
        val bitmap = createBitmap(view.width, view.height)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun observeUploadPaymentProofResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.uploadPaymentProofResult.collect { result ->
                    when (result) {
                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(this@PaymentResultActivity, result.error)
                            Log.d("PaymentResultActivity", result.error)
                        }
                        ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            showToast(this@PaymentResultActivity, result.data)
                            navigateBack()
                        }
                    }
                }
            }
        }
    }

    private fun navigateBack() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    companion object {
        const val EXTRA_PAYMENT_STATUS = "is_success"
        const val EXTRA_ORDER_ID = "order_id"
        const val EXTRA_AMOUNT = "amount"
    }
}