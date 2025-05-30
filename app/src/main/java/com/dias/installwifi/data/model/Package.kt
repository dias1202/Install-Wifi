package com.dias.installwifi.data.model

import com.dias.installwifi.R

data class Package(
    val name: String,
    val description: String,
    val termsAndConditions: String,
    val image: Int,
    val speed: Int,
    val price: Int
)

val Packages = listOf(
    Package(
        name = "Basic Package",
        description = "Suitable for light users",
        termsAndConditions = "Terms and conditions apply. Please read carefully.",
        image = R.drawable.paket_1,
        speed = 10,
        price = 150000
    ),
    Package(
        name = "Standard Package",
        description = "Ideal for regular users",
        termsAndConditions = "Terms and conditions apply. Please read carefully.",
        image = R.drawable.paket_2,
        speed = 50,
        price = 15
    ),
    Package(
        name = "Premium Package",
        description = "Best for heavy users and streaming",
        termsAndConditions = "Terms and conditions apply. Please read carefully.",
        image = R.drawable.paket_3,
        speed = 100,
        price = 30
    ),
    Package(
        name = "Ultimate Package",
        description = "Unlimited data and highest speed",
        termsAndConditions = "Terms and conditions apply. Please read carefully.",
        image = R.drawable.paket_4,
        speed = 200,
        price = 50
    ),
    Package(
        name = "Family Package",
        description = "Multiple users with shared benefits",
        termsAndConditions = "Terms and conditions apply. Please read carefully.",
        image = R.drawable.paket_5,
        speed = 150,
        price = 40
    )
)
