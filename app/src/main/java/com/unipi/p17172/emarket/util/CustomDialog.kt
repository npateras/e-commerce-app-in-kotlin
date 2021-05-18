package com.unipi.p17172.emarket.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.unipi.p17172.emarket.R
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class CustomDialog {
    companion object {
        fun showDialogNumberVerified(activity: Context) {
            val dialog = BottomSheetDialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            dialog.setContentView(R.layout.dialog_number_verified)
            val btnDismiss = dialog.findViewById<TextView>(R.id.btn_Try_Again)
            btnDismiss?.setOnClickListener { dialog.dismiss() }
            dialog.show()

            // Create an executor that executes tasks in a background thread.
            val backgroundExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

            // Execute a task in the background thread after 5 seconds.
            backgroundExecutor.schedule({
                dialog.dismiss()
            }, 5, java.util.concurrent.TimeUnit.SECONDS)
        }

        fun showDialogNoWifi(activity: Context) {
            val dialog = BottomSheetDialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_number_verified)

            val btnTryAgain = dialog.findViewById<TextView>(R.id.btn_Try_Again)
            btnTryAgain?.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}
