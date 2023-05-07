package com.unipi.mpsp21043.client.utils

import android.content.Context
import android.view.View
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

fun showMustSignInUI(context: Context,binding: ViewBinding) =
    binding.apply {
        when (binding) {
            is FragmentMyAccountBinding -> {
                binding.layoutErrorStateMustSignIn.apply {
                    root.visibility = View.VISIBLE
                    buttonSignIn.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                    buttonSignUp.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                }
            }
            is FragmentFavoritesBinding -> {
                binding.layoutErrorStateMustSignIn.apply {
                    root.visibility = View.VISIBLE
                    buttonSignIn.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                    buttonSignUp.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                }
            }
            is ActivityListOrdersBinding -> {
                binding.layoutErrorStateMustSignIn.apply {
                    root.visibility = View.VISIBLE
                    buttonSignIn.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                    buttonSignUp.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                }
            }
            is ActivityListCartItemsBinding -> {
                binding.layoutErrorStateMustSignIn.apply {
                    root.visibility = View.VISIBLE
                    buttonSignIn.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                    buttonSignUp.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                }
            }
            is ActivityListSettingsBinding -> {
                binding.layoutErrorStateMustSignIn.apply {
                    root.visibility = View.VISIBLE
                    buttonSignIn.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                    buttonSignUp.setOnClickListener { IntentUtils().goToSignInActivity(context) }
                }
            }
        }
    }
