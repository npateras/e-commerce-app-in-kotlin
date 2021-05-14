package com.unipi.p17172.emarket.activity

import android.app.ActionBar.DISPLAY_SHOW_CUSTOM
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapter.ViewPagerAdapter
import com.unipi.p17172.emarket.databinding.ActivityMainBinding
import com.unipi.p17172.emarket.fragment.FavoritesFragment
import com.unipi.p17172.emarket.fragment.HomeFragment
import com.unipi.p17172.emarket.fragment.MyAccountFragment
import com.unipi.p17172.emarket.fragment.MyCartFragment
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpUI()
        init()
    }

    private fun setUpUI() {
        setUpTabs()
        setUpActionBar()
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0)
                    binding.toolbar.textViewActionBarHeader.text = getString(R.string.txt_store)
                else if (tab.position == 1)
                    binding.toolbar.textViewActionBarHeader.text = getString(R.string.txt_my_cart)
                else if (tab.position == 2)
                    binding.toolbar.textViewActionBarHeader.text = getString(R.string.txt_favorites)
                else if (tab.position == 3)
                    binding.toolbar.textViewActionBarHeader.text = getString(R.string.txt_my_account)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)
        Objects.requireNonNull(supportActionBar)!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.let {
            it.displayOptions = DISPLAY_SHOW_CUSTOM
            it.setCustomView(R.layout.toolbar_activity_main)
        }

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar.root,
            R.string.nav_drawer_close, R.string.nav_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        // Change drawer arrow icon
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.colorTabSelected)
        toggle.setHomeAsUpIndicator(R.drawable.ic_list)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(R.id.nav_item_products)
    }

    private fun init() {

    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment())
        adapter.addFragment(MyCartFragment())
        adapter.addFragment(FavoritesFragment())
        adapter.addFragment(MyAccountFragment())
        binding.viewPagerHomeActivity.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPagerHomeActivity)

        // 0 : Home Fragment
        binding.tabs.getTabAt(0)!!.setIcon(R.drawable.ic_food_stand)
        binding.tabs.getTabAt(1)!!.setIcon(R.drawable.ic_shopping_cart)
        binding.tabs.getTabAt(2)!!.setIcon(R.drawable.ic_heart)
        binding.tabs.getTabAt(3)!!.setIcon(R.drawable.ic_user_circle)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_item_products) {
            binding.tabs.getTabAt(0)?.select()
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}