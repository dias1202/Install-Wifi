package com.dias.installwifi.data.repository

import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.local.pref.UserPreference
import com.dias.installwifi.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val userPreference: UserPreference
) {
    fun register(name: String, email: String, password: String): Flow<ResultState<User>> = flow {
        emit(ResultState.Loading)

        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID not found")

            val user = User(name, email)
            db.collection("users").document(uid).set(user).await()

            emit(ResultState.Success(user))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }

    fun login(email: String, password: String): Flow<ResultState<User>> = flow {
        emit(ResultState.Loading)
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID not found")

            val snapshot = db.collection("users").document(uid).get().await()
            val name = snapshot.getString("name") ?: ""
            val emailFromDb = snapshot.getString("email") ?: ""
            val createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()

            val user = User(uid, name, emailFromDb, createdAt, isLogin = true)

            emit(ResultState.Success(user))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Login failed"))
        }
    }

    fun loginWithGoogle(idToken: String): Flow<ResultState<User>> = flow {
        emit(ResultState.Loading)
        try {
            val authResult = auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            val firebaseUser = authResult.user ?: throw Exception("User not found")
            val uid = firebaseUser.uid
            val email = firebaseUser.email ?: throw Exception("Email not found")
            val name = firebaseUser.displayName ?: "No Name"
            val createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis()
            val photoUrl = firebaseUser.photoUrl?.toString()

            val userDocRef = db.collection("users").document(uid)
            val snapshot = userDocRef.get().await()

            val user: User

            if (!snapshot.exists()) {
                user = User(uid, name, email, createdAt, photoUrl, isGoogleLogin = true)
                userDocRef.set(user).await()
            } else {
                val existingName = snapshot.getString("name") ?: name
                val existingEmail = snapshot.getString("email") ?: email
                val existingCreatedAt = snapshot.getLong("createdAt") ?: createdAt
                user = User(uid, existingName, existingEmail, existingCreatedAt, isGoogleLogin = true)
            }

            emit(ResultState.Success(user))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Login failed"))
        }
    }

    fun saveSession(user: User) = flow {
        emit(ResultState.Loading)
        try {
            userPreference.saveSession(user)
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
}
