package com.unipi.p17172.emarket.ui.fragments

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.unipi.p17172.emarket.ui.activities.SignInActivity
import com.unipi.p17172.emarket.ui.activities.UpdateProfileActivity

open class BaseFragment : Fragment() {

    fun isSignedIn(fragment: Fragment) {
        when (fragment) {
            is HomeFragment -> {

            }
            is MyCartFragment -> {

            }
            is FavoritesFragment -> {

            }
            is MyAccountFragment -> {

            }
        }
    }

    fun goToSignInActivity(context: Context) {
        startActivity(Intent(context, SignInActivity::class.java))
    }

    fun goToUpdateProfileActivity(context: Context) {
        val intent: Intent = Intent(context, UpdateProfileActivity::class.java)
        startActivity(intent)

    }
}