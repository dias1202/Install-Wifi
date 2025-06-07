package com.dias.installwifi.data.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val packageId: Int = 0,
    val packageName: String = "",
    val packageSpeed: String = "",
    val price: Double = 0.0,
    val ppn: Double = 0.0,
    val installationFee: Double = 0.0,
    val discount: Double = 0.0,
    val totalPrice: Double = 0.0,
    val orderDate: Long = System.currentTimeMillis()
)
