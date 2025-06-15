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
    fun createOrder(order: Order): Flow<ResultState<Order>> = flow {
        emit(ResultState.Loading)
        try {
            val timestamp = System.currentTimeMillis()
            val docId = "order_$timestamp"

            val order = Order(
                id = docId,
                userId = order.userId,
                address = order.address,
                packageId = order.packageId,
                totalPrice = order.totalPrice,
                status = "PENDING",
                technicianId = null,
                orderDate = System.currentTimeMillis()
            )

            db.collection("orders").document(docId).set(order).await()
            emit(ResultState.Success(order))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }

    fun getOrdersByUserId(userId: String): Flow<ResultState<List<Order>>> = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val orders = snapshot.documents.map { document ->
                Order(
                    id = document.id,
                    userId = document.getString("userId") ?: "",
                    address = document.getString("address") ?: "",
                    packageId = document.getString("packageId") ?: "",
                    totalPrice = document.getLong("totalPrice")?.toInt() ?: 0,
                    status = document.getString("status") ?: "",
                    technicianId = document.getString("technicianId"),
                    orderDate = (document.getLong("orderDate") ?: System.currentTimeMillis())
                )
            }

            emit(ResultState.Success(orders))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }

    fun getOrdersByTechnicianId(technicianId: String): Flow<ResultState<List<Order>>> = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = db.collection("orders")
                .whereEqualTo("technicianId", technicianId)
                .get()
                .await()
            val orders = snapshot.documents.map { document ->
                Order(
                    id = document.id,
                    userId = document.getString("userId") ?: "",
                    address = document.getString("address") ?: "",
                    packageId = document.getString("packageId") ?: "",
                    totalPrice = document.getLong("totalPrice")?.toInt() ?: 0,
                    status = document.getString("status") ?: "",
                    technicianId = document.getString("technicianId"),
                    orderDate = (document.getLong("orderDate") ?: System.currentTimeMillis())
                )
            }
            emit(ResultState.Success(orders))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }
}
