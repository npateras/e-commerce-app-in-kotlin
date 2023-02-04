package com.unipi.mpsp21043.emarket.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.ui.activities.SignInActivity

open class BaseFragment : Fragment() {

    /**
     * This is a progress dialog instance which we will initialize later on.
     */
    private lateinit var mProgressDialog: Dialog
    // END

    fun goToSignInActivity(context: Context) {
        startActivity(Intent(context, SignInActivity::class.java))
    }

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog() {
        mProgressDialog = Dialog(this.requireContext())

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}
