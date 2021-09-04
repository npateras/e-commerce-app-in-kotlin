package com.unipi.p17172.emarket.utils

import android.app.Activity
import android.content.Intent
import com.unipi.p17172.emarket.ui.activities.AllCategoriesActivity
import com.unipi.p17172.emarket.ui.activities.ListProductsActivity

class IntentUtils {
    fun goToListProductsActivity(activity: Activity, filter: String) {
        val intent = Intent(activity, ListProductsActivity::class.java)
        intent.putExtra(Constants.EXTRA_FILTER, filter)
        activity.startActivity(intent)
    }

    fun goToCategoriesActivity(activity: Activity) {
        val intent = Intent(activity, AllCategoriesActivity::class.java)
        activity.startActivity(intent)
    }
}
