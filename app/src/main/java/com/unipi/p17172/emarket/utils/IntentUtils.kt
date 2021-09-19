package com.unipi.p17172.emarket.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.unipi.p17172.emarket.models.Address
import com.unipi.p17172.emarket.models.Order
import com.unipi.p17172.emarket.models.User
import com.unipi.p17172.emarket.ui.activities.*

class IntentUtils {
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

    fun goToPayWithCreditCardActivity(context: Context) {
        val intent = Intent(context, PayWithCreditCardActivity::class.java)
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
        context.applicationContext.startActivity(intent)
    }
}
