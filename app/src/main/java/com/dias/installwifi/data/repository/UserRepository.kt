package com.dias.installwifi.data.repository

import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.local.pref.UserPreference
import com.dias.installwifi.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val userPreference: UserPreference,
    private val supabaseClient: SupabaseClient
) {
    fun register(
        name: String,
        email: String,
        password: String,
        isTechnician: Boolean
    ): Flow<ResultState<User>> = flow {
        emit(ResultState.Loading)

        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID not found")

            val user = User(uid = uid, name = name, email = email, isLogin = true)

            db.collection(if (isTechnician) "technicians" else "users").document(uid).set(user)
                .await()

            emit(ResultState.Success(user))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }

    fun login(email: String, password: String, isTechnician: Boolean): Flow<ResultState<User>> =
        flow {
            emit(ResultState.Loading)
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("UID not found")

                val snapshot =
                    db.collection(if (isTechnician) "technicians" else "users").document(uid).get()
                        .await()
                val name = snapshot.getString("name") ?: ""
                val emailFromDb = snapshot.getString("email") ?: ""
                val createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()
                val photoUrl = snapshot.getString("photoUrl") ?: ""
                val phoneNumber = snapshot.getString("phoneNumber") ?: ""
                val address = snapshot.getString("address") ?: ""

                val user = User(uid, name, emailFromDb, createdAt, photoUrl, phoneNumber, address)

                emit(ResultState.Success(user))
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "Login failed"))
            }
        }

    fun loginWithGoogle(idToken: String, isTechnician: Boolean): Flow<ResultState<User>> = flow {
        emit(ResultState.Loading)
        try {
            val authResult =
                auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            val firebaseUser = authResult.user ?: throw Exception("User not found")
            val uid = firebaseUser.uid
            val email = firebaseUser.email ?: throw Exception("Email not found")
            val name = firebaseUser.displayName ?: "No Name"
            val createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis()
            val photoUrl = firebaseUser.photoUrl?.toString()
            val phoneNumber = firebaseUser.phoneNumber ?: ""
            val address = ""

            val userDocRef =
                db.collection(if (isTechnician) "technicians" else "users").document(uid)
            val snapshot = userDocRef.get().await()

            val user: User

            if (!snapshot.exists()) {
                user = User(
                    uid,
                    name,
                    email,
                    createdAt,
                    photoUrl,
                    phoneNumber,
                    address,
                    isGoogleLogin = true
                )
                userDocRef.set(user).await()
            } else {
                val existingName = snapshot.getString("name") ?: name
                val existingEmail = snapshot.getString("email") ?: email
                val existingCreatedAt = snapshot.getLong("createdAt") ?: createdAt
                val existingPhotoUrl = snapshot.getString("photoUrl") ?: photoUrl ?: ""
                val existingPhoneNumber = snapshot.getString("phoneNumber") ?: phoneNumber
                val existingAddress = snapshot.getString("address") ?: address

                user = User(
                    uid,
                    existingName,
                    existingEmail,
                    existingCreatedAt,
                    existingPhotoUrl,
                    existingPhoneNumber,
                    existingAddress,
                    isGoogleLogin = true
                )
            }

            emit(ResultState.Success(user))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Login failed"))
        }
    }

    fun saveSession(user: User, isTechnician: Boolean) = flow {
        emit(ResultState.Loading)
        try {
            userPreference.saveSession(user, isTechnician)
            emit(ResultState.Success(true))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Session not found"))
        }
    }

    fun getSession(): Flow<ResultState<User>> = flow {
        emit(ResultState.Loading)
        try {
            val user = userPreference.getSession().first()
            if (false) {
                emit(ResultState.Error("No session found"))
            } else {
                emit(ResultState.Success(user))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Session not found"))
        }
    }

    fun logout(): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        try {
            auth.signOut()
            userPreference.logout()
            emit(ResultState.Success(true))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Logout failed"))
        }
    }

    fun updateProfile(
        name: String,
        phoneNumber: String?,
        address: String?,
        file: File?
    ): Flow<ResultState<User>> = flow {
        emit(ResultState.Loading)
        try {
            val user = userPreference.getSession().first()
            val bucket = supabaseClient.storage.from("user-profile")
            var imageUrl: String? = null
            file?.let {
                val uniqueName = "profile_${System.currentTimeMillis()}.jpg"

                val storagePath = "users/${user.uid}/$uniqueName"

                bucket.upload(path = storagePath, file = file, options = { upsert = true })

                imageUrl = bucket.publicUrl(storagePath)
            }

            val userDocRef = db.collection("users").document(user.uid)
            val updateMap = mutableMapOf<String, Any>(
                "name" to name,
                "phoneNumber" to (phoneNumber ?: ""),
                "address" to (address ?: "")
            )
            if (imageUrl != null) updateMap["photoUrl"] = imageUrl
            userDocRef.update(updateMap).await()
            // Fetch updated user from Firestore
            val snapshot = userDocRef.get().await()
            val updatedUser = User(
                uid = user.uid,
                name = snapshot.getString("name") ?: name,
                email = snapshot.getString("email") ?: user.email,
                createdAt = snapshot.getLong("createdAt") ?: user.createdAt,
                photoUrl = snapshot.getString("photoUrl") ?: imageUrl ?: user.photoUrl,
                phoneNumber = snapshot.getString("phoneNumber") ?: phoneNumber,
                address = snapshot.getString("address") ?: address,
                isLogin = user.isLogin,
                isGoogleLogin = user.isGoogleLogin
            )
            emit(ResultState.Success(updatedUser))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Update failed"))
        }
    }

    fun getUserNameById(userId: String): Flow<ResultState<String?>> = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = db.collection("users").document(userId).get().await()
            val name = snapshot.getString("name")
            emit(ResultState.Success(name))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Failed to get user name"))
        }
    }
}
