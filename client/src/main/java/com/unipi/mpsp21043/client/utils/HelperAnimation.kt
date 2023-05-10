package com.unipi.mpsp21043.client.utils

import android.content.Context
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewbinding.ViewBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.behavior.SwipeDismissBehavior.SWIPE_DIRECTION_ANY
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.textfield.TextInputLayout
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.databinding.ActivityListCartItemsBinding
import com.unipi.mpsp21043.client.databinding.ActivityListOrdersBinding
import com.unipi.mpsp21043.client.databinding.ActivityListSettingsBinding
import com.unipi.mpsp21043.client.databinding.FragmentFavoritesBinding
import com.unipi.mpsp21043.client.databinding.FragmentMyAccountBinding

fun animationSlideUp(view: View): TranslateAnimation {
    val animation = TranslateAnimation(
        0f,  // fromXDelta
        0f,  // toXDelta
        view.height.toFloat(),  // fromYDelta
        0f
    ) // toYDelta
    animation.duration = 400
    animation.fillAfter = true

    return animation
}
