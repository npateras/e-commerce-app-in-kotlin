package com.unipi.p17172.emarket.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.unipi.p17172.emarket.ui.fragments.FavoritesFragment
import com.unipi.p17172.emarket.ui.fragments.HomeFragment
import com.unipi.p17172.emarket.ui.fragments.MyAccountFragment

class ViewPagerMainAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val fragmentsList:ArrayList<Fragment> = arrayListOf(
        HomeFragment(),
        FavoritesFragment(),
        MyAccountFragment()
    )

    override fun getItemCount(): Int {
        return fragmentsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position]
    }
}
