package com.unipi.mpsp21043.admin.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.unipi.mpsp21043.admin.ui.fragments.OrdersFragment
import com.unipi.mpsp21043.admin.ui.fragments.ProductsFragment
import com.unipi.mpsp21043.admin.ui.fragments.UsersFragment

class ViewPagerMainAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val fragmentsList: ArrayList<Fragment> = arrayListOf(
        ProductsFragment(),
        OrdersFragment(),
        UsersFragment()
    )

    override fun getItemCount(): Int {
        return fragmentsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position]
    }
}
