package com.dias.installwifi.view.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.dias.installwifi.R
import com.google.android.material.textfield.TextInputLayout

class CustomEditTextEmail : AppCompatAutoCompleteTextView {

    private var textInputLayout: TextInputLayout? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        textAlignment = TEXT_ALIGNMENT_VIEW_START

        val emailSuggestions = listOf(
            "example@gmail.com",
            "admin@gmail.com",
            "user@yahoo.com",
            "test@hotmail.com"
        )

        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, emailSuggestions)
        setAdapter(adapter)
        threshold = 1

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val parentLayout = parent?.parent
                if (parentLayout is TextInputLayout) {
                    textInputLayout = parentLayout
                }

                validateInput(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun validateInput(input: String) {
        when {
            input.isEmpty() -> {
                textInputLayout?.error = context.getString(R.string.empty_email_error)
                textInputLayout?.isErrorEnabled = true
            }

            !Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
                textInputLayout?.error = context.getString(R.string.invalid_format_email)
                textInputLayout?.isErrorEnabled = true
            }

            else -> {
                textInputLayout?.error = null
                textInputLayout?.isErrorEnabled = false
            }
        }
    }
}