package com.dias.installwifi.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dias.installwifi.data.model.Order
import com.dias.installwifi.data.repository.OrderRepository
import com.dias.installwifi.data.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderResult = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val orderResult: StateFlow<ResultState<String>> = _orderResult

    fun postOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.postOrder(order).collect {
                _orderResult.value = it
            }
        }
    }
}
