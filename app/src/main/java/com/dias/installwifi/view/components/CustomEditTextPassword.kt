package com.dias.installwifi.view.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dias.installwifi.R
import com.google.android.material.textfield.TextInputLayout

class CustomEditTextPassword : AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        textAlignment = TEXT_ALIGNMENT_VIEW_START
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                val parentLayout = parent.parent as? TextInputLayout
                val errorMessage = validatePassword(password)

                if (errorMessage != null) {
                    parentLayout?.error = errorMessage
                    parentLayout?.helperText = null
                    parentLayout?.isHelperTextEnabled = false
                } else {
                    parentLayout?.error = null
                    parentLayout?.helperText = context.getString(R.string.password_valid)
                    parentLayout?.isHelperTextEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        post {
            val parentLayout = parent.parent as? TextInputLayout
            parentLayout?.helperText =
                context.getString(R.string.password_must_be_at_least_8_characters_contain_numbers_and_capital_letters)
            parentLayout?.isHelperTextEnabled = true
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> context.getString(R.string.password_cannot_be_blank)
            password.length < 8 -> context.getString(R.string.password_minimum_8_characters)
            !password.any { it.isDigit() } -> context.getString(R.string.password_must_contain_numbers)
            !password.any { it.isUpperCase() } -> context.getString(R.string.password_must_contain_capital_letters)
            else -> null
        }
    }
}