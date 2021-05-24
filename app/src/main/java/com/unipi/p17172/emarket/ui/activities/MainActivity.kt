package com.unipi.p17172.emarket.ui.activities

import android.app.ActionBar.DISPLAY_SHOW_CUSTOM
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapters.ViewPagerMainAdapter
import com.unipi.p17172.emarket.databinding.ActivityMainBinding
import com.unipi.p17172.emarket.ui.fragments.FavoritesFragment
import com.unipi.p17172.emarket.ui.fragments.HomeFragment
import com.unipi.p17172.emarket.ui.fragments.MyAccountFragment
import com.unipi.p17172.emarket.ui.fragments.MyCartFragment
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

        setupNavDrawer()
    }

    private fun setupNavDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar.root,
            R.string.nav_drawer_close, R.string.nav_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        // Change drawer arrow icon
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.colorTabSelected)
        // Set navigation arrow icon
        toggle.setHomeAsUpIndicator(R.drawable.ic_list)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(R.id.nav_drawer_item_products)
    }

    private fun init() {

    }

    private fun setUpTabs() {
        val adapter = ViewPagerMainAdapter(supportFragmentManager)
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

        when (item.itemId) {
            R.id.nav_drawer_item_products -> {
                binding.tabs.getTabAt(0)?.select()
            }
            R.id.nav_drawer_item_favorites -> {
                binding.tabs.getTabAt(2)?.select()
            }
            R.id.nav_drawer_item_cart -> {
                binding.tabs.getTabAt(1)?.select()
            }
            R.id.nav_drawer_item_profile -> {
                binding.tabs.getTabAt(3)?.select()
            }
            R.id.nav_drawer_item_orders -> {
                val intent = Intent(this@MainActivity, MyOrdersActivity::class.java)
                startActivity(intent)
                return false
            }
            R.id.nav_drawer_item_settings -> {
                /*val intent = Intent(this@MainActivity, Settings::class.java)
                startActivity(intent)*/
                return false
            }
            R.id.nav_drawer_item_exit -> {
                ActivityCompat.finishAffinity(this)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}