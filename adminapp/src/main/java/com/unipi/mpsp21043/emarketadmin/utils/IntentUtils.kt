package com.unipi.mpsp21043.emarketadmin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.unipi.mpsp21043.emarketadmin.models.Address
import com.unipi.mpsp21043.emarketadmin.models.Order
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.models.User
import com.unipi.mpsp21043.emarketadmin.ui.activities.*

class IntentUtils {

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

    fun goToAddNewProductActivity(context: Context) {
        // Launch add/edit product screen.
        val intent = Intent(context, AddEditProductActivity::class.java)
        context.startActivity(intent)
    }

    fun goToEditProductActivity(context: Context, mProduct: Product) {
        // Launch add/edit product screen.
        val intent = Intent(context, AddEditProductActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRODUCT_MODEL, mProduct)
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

    fun goToSearchActivity(context: Context) {
        val intent = Intent(context, SearchActivity::class.java)
        context.startActivity(intent)
    }

    fun goToProductDetailsActivity(context: Context, productId: String) {
        // Launch Product details screen.
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, productId)
        context.startActivity(intent)
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
