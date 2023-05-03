package com.unipi.mpsp21043.admin.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.unipi.mpsp21043.admin.R

class DialogUtils {

    fun showDialogSelectCategory(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_select_category)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

       /* val btnAlertLangOk = dialog.findViewById<MaterialButton>(R.id.btnAlertProfileSaved_Ok)
        btnAlertLangOk.setOnClickListener { v: View? -> dialog.dismiss() }*/

        return dialog
    }

    fun showDialogSelectWeightUnit(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_select_weight_unit)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /* val btnAlertLangOk = dialog.findViewById<MaterialButton>(R.id.btnAlertProfileSaved_Ok)
         btnAlertLangOk.setOnClickListener { v: View? -> dialog.dismiss() }*/

        return dialog
    }

    fun showDialogSelectSortProducts(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_select_sort_products)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    fun showDialogSelectSortOrders(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_select_sort_orders)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    fun showDialogSelectSortUsers(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_select_sort_users)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    fun showDialogDeleteConfirmation(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_delete_confirmation)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }
}
