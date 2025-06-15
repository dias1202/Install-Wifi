package com.dias.installwifi.view.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Order
import com.dias.installwifi.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _orderResult = MutableStateFlow<ResultState<Order>>(ResultState.Loading)
    val orderResult: StateFlow<ResultState<Order>> = _orderResult

    private val _getOrderByUserId = MutableStateFlow<ResultState<List<Order>>>(ResultState.Loading)
    val getOrderByUserId: StateFlow<ResultState<List<Order>>> = _getOrderByUserId

    private val _getOrdersByTechnicianIdResult =
        MutableStateFlow<ResultState<List<Order>>>(ResultState.Loading)
    val getOrdersByTechnicianIdResult: StateFlow<ResultState<List<Order>>> =
        _getOrdersByTechnicianIdResult

    private val _uploadPaymentProofResult =
        MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val uploadPaymentProofResult: StateFlow<ResultState<String>> = _uploadPaymentProofResult

    fun createOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.createOrder(order).collect {
                _orderResult.value = it
            }
        }
    }

    fun getOrdersByUserId(userId: String) {
        viewModelScope.launch {
            orderRepository.getOrdersByUserId(userId).collect {
                _getOrderByUserId.value = it
            }
        }
    }

    fun getOrdersByTechnicianId(technicianId: String) {
        viewModelScope.launch {
            orderRepository.getOrdersByTechnicianId(technicianId).collect {
                _getOrdersByTechnicianIdResult.value = it
            }
        }
    }

    fun uploadPaymentProof(orderId: String, file: Bitmap) {
        viewModelScope.launch {
            orderRepository.uploadPaymentProof(orderId, file).collect {
                _uploadPaymentProofResult.value = it
            }
        }
    }
}
