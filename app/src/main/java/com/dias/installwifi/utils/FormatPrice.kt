package com.dias.installwifi.utils

import java.text.NumberFormat
import java.util.Locale

object FormatPrice {
    fun formatPrice(price: Int): String {
        val formattedPrice = NumberFormat.getInstance(Locale("in", "ID")).format(price)
        return "$formattedPrice"
    }

}