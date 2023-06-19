package com.unipi.mpsp21043.client.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.unipi.mpsp21043.client.models.Address
import com.unipi.mpsp21043.client.models.Order
import com.unipi.mpsp21043.client.models.User
import com.unipi.mpsp21043.client.ui.activities.AddEditAddressActivity
import com.unipi.mpsp21043.client.ui.activities.AllCategoriesActivity
import com.unipi.mpsp21043.client.ui.activities.CheckoutActivity
import com.unipi.mpsp21043.client.ui.activities.ForgotPasswordActivity
import com.unipi.mpsp21043.client.ui.activities.ListAddressActivity
import com.unipi.mpsp21043.client.ui.activities.ListCartItemsActivity
import com.unipi.mpsp21043.client.ui.activities.ListOrdersActivity
import com.unipi.mpsp21043.client.ui.activities.ListProductsActivity
import com.unipi.mpsp21043.client.ui.activities.ListSettingsActivity
import com.unipi.mpsp21043.client.ui.activities.MainActivity
import com.unipi.mpsp21043.client.ui.activities.OrderDetailsActivity
import com.unipi.mpsp21043.client.ui.activities.PayWithCreditCardActivity
import com.unipi.mpsp21043.client.ui.activities.ProductDetailsActivity
import com.unipi.mpsp21043.client.ui.activities.SignInActivity
import com.unipi.mpsp21043.client.ui.activities.SignUpActivity
import com.unipi.mpsp21043.client.ui.activities.AuthenticateActivity
import com.unipi.mpsp21043.client.ui.activities.ChangePasswordActivity
import com.unipi.mpsp21043.client.ui.activities.EditProfileActivity

class IntentUtils {

    fun createNewMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        (context as Activity).finishAffinity()
        context.startActivity(intent)
    }

    fun goToAuthenticateActivity(context: Context) {
        val intent = Intent(context, AuthenticateActivity::class.java)
        context.startActivity(intent)
    }

    fun goToForgotPasswordActivity(context: Context) {
        // Launch the forgot password screen when the user clicks on the forgot password text.
        val intent = Intent(context, ForgotPasswordActivity::class.java)
        context.startActivity(intent)
    }

    fun goToSignUpActivity(context: Context) {
        // Launch the sign up screen when the user clicks on the sign up text.
        val intent = Intent(context, SignUpActivity::class.java)
        context.startActivity(intent)
    }

    fun goToSignInActivity(context: Context) {
        // Launch the sign up screen when the user clicks on the sign up text.
        val intent = Intent(context, SignInActivity::class.java)
        context.startActivity(intent)
    }

    fun goToListProductsActivity(context: Context, filter: String) {
        // Launch List Products screen.
        val intent = Intent(context, ListProductsActivity::class.java)
        intent.putExtra(Constants.EXTRA_CATEGORY_NAME, filter)
        context.startActivity(intent)
    }

    fun goToCategoriesActivity(activity: Activity) {
        val intent = Intent(activity, AllCategoriesActivity::class.java)
        activity.startActivity(intent)
    }

    fun goToUpdateUserDetailsActivity(context: Context, user: User) {
        val intent = Intent(context, EditProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToListSettingsActivity(context: Context) {
        val intent = Intent(context, ListSettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToChangePasswordActivity(context: Context) {
        val intent = Intent(context, ChangePasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToListAddressesActivity(context: Context) {
        val intent = Intent(context, ListAddressActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToListAddressesActivity(context: Context, selectAddress: Boolean) {
        val intent = Intent(context, ListAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, selectAddress)
        context.startActivity(intent)
    }

    fun goToCheckoutActivity(context: Context, mSelectedAddress: Address) {
        val intent = Intent(context, CheckoutActivity::class.java)
        intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, mSelectedAddress)
        context.startActivity(intent)
    }

    fun goToAddNewAddressActivity(context: Context) {
        // Launch add/edit user address screen.
        val intent = Intent(context, AddEditAddressActivity::class.java)
        context.startActivity(intent)
    }

    fun goToEditAddressActivity(context: Context, mUserAddress: Address) {
        // Launch add/edit user address screen.
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_MODEL, mUserAddress)
        context.startActivity(intent)
    }

    fun goToProductDetailsActivity(context: Context, productId: String) {
        // Launch Product details screen.
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, productId)
        context.startActivity(intent)
    }

    fun goToCheckoutActivity(context: Context) {
        val intent = Intent(context, CheckoutActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToListOrdersActivity(context: Context) {
        val intent = Intent(context, ListOrdersActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToListCartItemsActivity(context: Context) {
        val intent = Intent(context, ListCartItemsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToPayWithCreditCardActivity(context: Context, mAddress: Address) {
        val intent = Intent(context, PayWithCreditCardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, mAddress)
        context.applicationContext.startActivity(intent)
    }

    fun goToMainActivity(context: Context, showOrderPlacedSnackbar: Boolean) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.EXTRA_SHOW_ORDER_PLACED_SNACKBAR, showOrderPlacedSnackbar)
        context.startActivity(intent)
    }

    fun goToOrderDetailsActivity(context: Context, order: Order) {
        val intent = Intent(context, OrderDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_ORDER_DETAILS, order)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }
}
