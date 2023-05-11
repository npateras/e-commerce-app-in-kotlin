package com.unipi.mpsp21043.client.utils

import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.behavior.SwipeDismissBehavior.SWIPE_DIRECTION_ANY
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.textfield.TextInputLayout
import com.unipi.mpsp21043.client.R

fun snackBarSuccessClass(view: View, message: String) =
    SnackBarSuccessClass
        .make(view, message)
        .setBehavior(BaseTransientBottomBar.Behavior().apply { setSwipeDirection(SWIPE_DIRECTION_ANY) })
        .show()

fun snackBarSuccessClass(view: View, message: String, anchor: View) =
    SnackBarSuccessClass
        .make(view, message)
        .setBehavior(BaseTransientBottomBar.Behavior().apply { setSwipeDirection(SWIPE_DIRECTION_ANY) })
        .setAnchorView(anchor)
        .show()

fun snackBarSuccessLargeClass(view: View, message: String) =
    SnackBarSuccessLargeClass
        .make(view, message)
        .setBehavior(BaseTransientBottomBar.Behavior().apply { setSwipeDirection(SWIPE_DIRECTION_ANY) })
        .show()

fun snackBarErrorClass(view: View, message: String) =
    SnackBarErrorClass
        .make(view, message)
        .setBehavior(BaseTransientBottomBar.Behavior().apply { setSwipeDirection(SWIPE_DIRECTION_ANY) })
        .show()

fun snackBarErrorClass(view: View, message: String, anchor: View) =
    SnackBarErrorClass
        .make(view, message)
        .setBehavior(BaseTransientBottomBar.Behavior().apply { setSwipeDirection(SWIPE_DIRECTION_ANY) })
        .setAnchorView(anchor)
        .show()

fun Context.textInputLayoutError(view: TextInputLayout) =
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

@androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
fun createBadge(context: Context, view: View, number: Int): BadgeDrawable {
    val badgeDrawable: BadgeDrawable = BadgeDrawable.create(context)
    badgeDrawable.number = number
    badgeDrawable.apply {
        isVisible = true
        maxCharacterCount = 10
        backgroundColor = context.getColor(R.color.colorRedLight)
        badgeTextColor = context.getColor(R.color.colorWhite)
    }
    BadgeUtils.attachBadgeDrawable(badgeDrawable, view)
    return badgeDrawable
}
