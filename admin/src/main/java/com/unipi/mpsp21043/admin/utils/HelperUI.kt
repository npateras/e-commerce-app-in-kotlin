package com.unipi.mpsp21043.admin.utils

import android.content.Context
import android.view.View
import androidx.viewbinding.ViewBinding
import com.unipi.mpsp21043.admin.databinding.ActivityEditProfileBinding

fun showProgressBarHorizontalTop(context: Context,binding: ViewBinding) =
    binding.apply {
        when (binding) {
            is ActivityEditProfileBinding -> {
                binding.progressBarLayout.progressBarHorizontal.visibility = View.VISIBLE
            }
        }
    }

fun hideProgressBarHorizontalTop(context: Context,binding: ViewBinding) =
    binding.apply {
        when (binding) {
            is ActivityEditProfileBinding -> {
                binding.progressBarLayout.progressBarHorizontal.visibility = View.INVISIBLE
            }
        }
    }
