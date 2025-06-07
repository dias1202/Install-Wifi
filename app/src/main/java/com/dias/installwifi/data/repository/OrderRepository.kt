package com.dias.installwifi.data.repository

import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun postOrder(order: Order): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val docRef = db.collection("orders").document()
            val orderWithId = order.copy(orderId = docRef.id)
            docRef.set(orderWithId).await()
            emit(ResultState.Success(docRef.id))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }
}

