package com.unipi.mpsp21043.emarketadmin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.unipi.mpsp21043.emarketadmin.models.Order
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.models.User
import com.unipi.mpsp21043.emarketadmin.ui.activities.*

class IntentUtils {

    fun goToSignInActivity(context: Context) {
        val intent = Intent(context, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun goToMyAccountActivity(context: Context) {
        val intent = Intent(context, MyAccountActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToUpdateUserDetailsActivity(context: Context, user: User) {
        val intent = Intent(context, EditProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
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

    fun goToProductDetailsActivity(context: Context, productId: String) {
        // Launch Product details screen.
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, productId)
        context.startActivity(intent)
    }

    fun goToUserDetailsActivity(context: Context, userId: String) {
        // Launch Product details screen.
        val intent = Intent(context, UserDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_ID, userId)
        context.startActivity(intent)
    }

    fun goToOrderDetailsActivity(context: Context, order: Order) {
        val intent = Intent(context, OrderDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_ORDER_DETAILS, order)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }
}
