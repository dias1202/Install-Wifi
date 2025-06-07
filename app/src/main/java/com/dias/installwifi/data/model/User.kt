package com.dias.installwifi.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val isLogin: Boolean? = false,
    val isGoogleLogin: Boolean? = false,
)