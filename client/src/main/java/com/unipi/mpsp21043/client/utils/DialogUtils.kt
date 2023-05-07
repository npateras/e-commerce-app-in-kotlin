package com.unipi.mpsp21043.client.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.unipi.mpsp21043.client.R

object DialogUtils {

    fun dialogTheme(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_settings_theme)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

       /* val btnAlertLangOk = dialog.findViewById<MaterialButton>(R.id.btnAlertProfileSaved_Ok)
        btnAlertLangOk.setOnClickListener { v: View? -> dialog.dismiss() }*/

        return dialog
    }
}
