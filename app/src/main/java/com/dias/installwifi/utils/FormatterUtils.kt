package com.dias.installwifi.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object FormatterUtils {
    fun formatPrice(price: Int): String {
        val formattedPrice = NumberFormat.getInstance(Locale("in", "ID")).format(price)
        return "$formattedPrice"
    }

    fun formatDate(date: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

}