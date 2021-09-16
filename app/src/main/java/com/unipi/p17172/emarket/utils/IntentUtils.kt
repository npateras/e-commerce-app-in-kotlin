package com.unipi.p17172.emarket.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.unipi.p17172.emarket.models.Address
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
}
