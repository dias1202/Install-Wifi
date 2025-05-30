package com.dias.installwifi.data.model

import com.dias.installwifi.R

data class Banner(
    val title: String,
    val description: String,
    val image: Int,
)

val Banners = listOf(
    Banner(
        title = "Banner 1",
        description = "Banner 1",
        image = R.drawable.banner_1
    ),
    Banner(
        title = "Banner 2",
        description = "Banner 2",
        image = R.drawable.banner_2
    ),
    Banner(
        title = "Banner 3",
        description = "Banner 3",
        image = R.drawable.banner_3
    )
)

val BannerPromo = listOf(
    Banner(
        title = "Promo 1",
        description = "Promo 1",
        image = R.drawable.banner_promo_1
    ),
    Banner(
        title = "Promo 2",
        description = "Promo 2",
        image = R.drawable.banner_promo_2
    ),
    Banner(
        title = "Promo 3",
        description = "Promo 3",
        image = R.drawable.banner_promo_3
    ),
    Banner(
        title = "Promo 4",
        description = "Promo 4",
        image = R.drawable.banner_promo_4
    ),
)