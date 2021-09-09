package com.unipi.p17172.emarket.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.unipi.p17172.emarket.models.User
import com.unipi.p17172.emarket.ui.activities.AllCategoriesActivity
import com.unipi.p17172.emarket.ui.activities.ListProductsActivity
import com.unipi.p17172.emarket.ui.activities.ProductDetailsActivity
import com.unipi.p17172.emarket.ui.activities.UpdateProfileActivity

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
        val intent = Intent(context, UpdateProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun goToProductDetailsActivity(context: Context, productId: String, isInFavorites: Boolean) {
        // Launch Product details screen.
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, productId)
        intent.putExtra(Constants.EXTRA_IS_IN_FAVORITES, isInFavorites)
        context.startActivity(intent)
    }
}
