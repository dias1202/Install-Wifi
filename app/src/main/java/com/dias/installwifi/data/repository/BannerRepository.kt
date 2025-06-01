package com.dias.installwifi.data.repository

import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Banner
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BannerRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun getBanners() = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = db.collection("banner").get().await()
            val packages = snapshot.documents.map { document ->
                Banner(
                    id = document.id.toIntOrNull() ?: 0,
                    title = document.getString("title") ?: "",
                    description = document.getString("description") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                )
            }
            emit(ResultState.Success(packages))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Failed to fetch packages"))
        }
    }

    fun getBannerPromo() = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = db.collection("banner_promo").get().await()
            val bannerPromos = snapshot.documents.map { document ->
                Banner(
                    id = document.id.toIntOrNull() ?: 0,
                    title = document.getString("title") ?: "",
                    description = document.getString("description") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                )
            }
            emit(ResultState.Success(bannerPromos))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Failed to fetch banner promos"))
        }
    }

    fun getBannerPromoById(bannerId: Int) = flow {
        emit(ResultState.Loading)
        try {
            val document = db.collection("banner_promo").document(bannerId.toString()).get().await()
            if (document.exists()) {
                val banner = Banner(
                    id = document.id.toIntOrNull() ?: 0,
                    title = document.getString("title") ?: "",
                    description = document.getString("description") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    termsAndConditions = document.getString("termsAndConditions") ?: ""
                )
                emit(ResultState.Success(banner))
            } else {
                emit(ResultState.Error("Banner not found"))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Failed to fetch banner"))
        }
    }
}