package com.dias.installwifi.utils

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch

fun <T> setupDropdown(
    context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    dropdown: MaterialAutoCompleteTextView,
    options: List<String>,
    getValue: suspend () -> T,
    setValue: suspend (T) -> Unit,
    mapToIndex: (T) -> Int,
    mapFromIndex: (Int) -> T
) {
    val adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, options)
    dropdown.setAdapter(adapter)
    lifecycleScope.launch {
        val value = getValue()
        dropdown.setText(options[mapToIndex(value)], false)
    }
    dropdown.setOnItemClickListener { _, _, position, _ ->
        lifecycleScope.launch {
            setValue(mapFromIndex(position))
        }
    }
}