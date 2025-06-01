package com.dias.installwifi.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Package(
    val id: Int = 0,
    val name: String? = "",
    val description: String? = "",
    val termsAndConditions: String? = "",
    val imageUrl: String? = "",
    val imageHorizontalUrl: String? = "",
    val speed: Int? = 0,
    val price: Int? = 0,
) : Parcelable


