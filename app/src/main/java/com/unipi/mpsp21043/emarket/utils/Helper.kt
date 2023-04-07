package com.unipi.mpsp21043.emarket.utils

import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputLayout
import com.unipi.mpsp21043.emarket.R

fun snackBarSuccessClass(view: View, message: String) =
    SnackBarSuccessClass.make(view, message).show()

fun snackBarErrorClass(view: View, message: String) =
    SnackBarErrorClass.make(view, message).show()

fun Context.textInputLayoutError(view: TextInputLayout, message: String) =
    view.apply {
        requestFocus()
        error = getString(R.string.text_error_empty_email)
        background = AppCompatResources.getDrawable(this.context, R.drawable.text_input_background_error)
    }

fun textInputLayoutNormal(view: TextInputLayout) =
    view.apply {
        isErrorEnabled = false
        background = AppCompatResources.getDrawable(this.context, R.drawable.text_input_background)
    }

class Helper {}
