package com.dias.installwifi.data.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val address: String = "",
    val packageId: String = "",
    val totalPrice: Int = 0,
    val status: String = "PENDING",
    val technicianId: String? = null,
    val orderDate: Long = System.currentTimeMillis()
)
