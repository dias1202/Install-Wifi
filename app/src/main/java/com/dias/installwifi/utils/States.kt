package com.dias.installwifi.utils

import android.content.Context
import android.view.View
import android.widget.Toast

object States {

    fun showLoading(view: View, state: Boolean) {
        if (state) view.visibility = View.VISIBLE else view.visibility = View.GONE
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}