package com.dias.installwifi.data.repository

import android.graphics.Bitmap
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val supabaseClient: SupabaseClient
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
                paymentProofUrl = null,
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

    fun uploadPaymentProof(orderId: String, file: Bitmap): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val bucket = supabaseClient.storage.from("payment-proofs")
            val filename = "payment_proof_$orderId.jpg"
            val storagePath = "orders/$orderId/$filename"

            val outputStream = ByteArrayOutputStream()
            file.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val byteArray = outputStream.toByteArray()

            bucket.upload(path = storagePath, data = byteArray)
            val publicUrl = bucket.publicUrl(storagePath)

            db.collection("orders").document(orderId)
                .update("paymentProofUrl", publicUrl)
                .await()

            emit(ResultState.Success(publicUrl))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }
}
