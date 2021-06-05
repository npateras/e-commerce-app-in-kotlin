package com.unipi.p17172.emarket.ui.fragments

import androidx.fragment.app.Fragment

class BaseFragment : Fragment() {

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

}