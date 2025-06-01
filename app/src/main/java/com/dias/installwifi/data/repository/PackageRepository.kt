package com.dias.installwifi.data.repository

import com.dias.installwifi.data.ResultState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.dias.installwifi.data.model.Package

class PackageRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun getPackages() = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = db.collection("package").get().await()
            val packages = snapshot.documents.map { document ->
                Package(
                    id = document.id.toIntOrNull() ?: 0,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    termsAndConditions = document.getString("termsAndConditions") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    imageHorizontalUrl = document.getString("imageHorizontalUrl") ?: "",
                    speed = document.getLong("speed")?.toInt() ?: 0,
                    price = document.getLong("price")?.toInt() ?: 0,
                )
            }
            emit(ResultState.Success(packages))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Failed to fetch packages"))
        }
    }

    fun getPackageById(packageId: Int) = flow {
        emit(ResultState.Loading)
        try {
            val document = db.collection("package").document(packageId.toString()).get().await()
            if (document.exists()) {
                val packageData = Package(
                    id = document.id.toIntOrNull() ?: 0,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    termsAndConditions = document.getString("termsAndConditions") ?: "",
                    imageHorizontalUrl = document.getString("imageHorizontalUrl") ?: "",
                    speed = document.getLong("speed")?.toInt() ?: 0,
                    price = document.getLong("price")?.toInt() ?: 0,
                )
                emit(ResultState.Success(packageData))
            } else {
                emit(ResultState.Error("Package not found"))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Failed to fetch package"))
        }
    }
}