package com.unipi.mpsp21043.emarketadmin.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.adapters.ViewPagerMainAdapter
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.ActivityMainBinding
import com.unipi.mpsp21043.emarketadmin.models.User
import com.unipi.mpsp21043.emarketadmin.service.MyFirebaseMessagingService
import com.unipi.mpsp21043.emarketadmin.utils.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        init()
    }

    private fun setupUI() {
        setupTabs()
        setupActionBar()
        setupNavDrawer()

        binding.toolbar.actionBarButtonSearch.setOnClickListener { IntentUtils().goToSearchActivity(this) }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_activity_main)
        }
    }

    private fun setupNavDrawer() {
        binding.apply {
            val toggle = ActionBarDrawerToggle(
                this@MainActivity, drawerLayout, toolbar.root,
                R.string.nav_drawer_open_menu, R.string.nav_drawer_open_menu
            )
            drawerLayout.addDrawerListener(toggle)
            // Change drawer arrow icon
            toggle.drawerArrowDrawable.color =
                ContextCompat.getColor(this@MainActivity, R.color.colorTabSelected)
            // Set navigation arrow icon
            toggle.setHomeAsUpIndicator(R.drawable.svg_list)
            toggle.syncState()

            navView.inflateHeaderView(R.layout.nav_drawer_header_signed_in)
            FirestoreHelper().getUserDetails(this@MainActivity)
            navView.setNavigationItemSelectedListener(this@MainActivity)
            navView.setCheckedItem(R.id.nav_drawer_item_products)
        }
    }

    private fun init() {
        if (FirestoreHelper().getCurrentUserID() != "") {
            FirestoreHelper().getUserFCMRegistrationToken(this)
        }

        if (intent.hasExtra(Constants.EXTRA_SNACKBAR_TYPE)) {
            if (intent.getStringExtra(Constants.EXTRA_SNACKBAR_TYPE) == Constants.STATUS_SUCCESS) {
                SnackBarSuccessClass
                    .make(binding.root, intent.getStringExtra(Constants.EXTRA_SNACKBAR_MESSAGE)!!)
                    .show()
            }
            else if (intent.getStringExtra(Constants.EXTRA_SNACKBAR_TYPE) == Constants.STATUS_ERROR) {
                SnackBarErrorClass
                    .make(binding.root, intent.getStringExtra(Constants.EXTRA_SNACKBAR_MESSAGE)!!)
                    .show()
            }

        }
    }

    private fun setupTabs() {
        val adapter = ViewPagerMainAdapter(supportFragmentManager, lifecycle)

        binding.apply {
            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.getTabAt(position)?.select()
                }
            })

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.setIcon(R.drawable.svg_food_stand_products)
                    1 -> tab.setIcon(R.drawable.svg_orders)
                    2 -> tab.setIcon(R.drawable.svg_users)
                }
            }.attach()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> {
                            toolbar.textViewActionBarHeader.text = getString(R.string.txt_products)
                            navView.setCheckedItem(R.id.nav_drawer_item_products)
                        }
                        1 -> {
                            toolbar.textViewActionBarHeader.text = getString(R.string.txt_orders)
                            navView.setCheckedItem(R.id.nav_drawer_item_orders)
                        }
                        2 -> {
                            toolbar.textViewActionBarHeader.text = getString(R.string.txt_users)
                            navView.setCheckedItem(R.id.nav_drawer_item_users)
                        }
                    }
                    viewPager.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        binding.apply {
            when (item.itemId) {
                R.id.nav_drawer_item_products -> tabLayout.getTabAt(0)?.select()
                R.id.nav_drawer_item_orders -> tabLayout.getTabAt(1)?.select()
                R.id.nav_drawer_item_users -> tabLayout.getTabAt(2)?.select()
                R.id.nav_drawer_item_profile -> {
                    IntentUtils().goToMyAccountActivity(this@MainActivity)
                    return false
                }
                R.id.nav_drawer_item_exit -> ActivityCompat.finishAffinity(this@MainActivity)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun userDetailsSuccess(userInfo: User) {
        binding.apply {
            navView.getHeaderView(0).apply {
                findViewById<TextView>(R.id.navDrawer_SignedIn_Full_Name)
                    .text = userInfo.fullName
                findViewById<TextView>(R.id.navDrawer_SignedIn_Email)
                    .text = userInfo.email
            }

            // Check if user is ADMIN and then show the admin features in nav drawer bar.
            if (userInfo.role == Constants.ROLE_ADMIN) {
                // And also add the admin icon next to the name in nav drawer header.
                navView.getHeaderView(0).apply {
                    findViewById<TextView>(R.id.navDrawer_SignedIn_Full_Name)
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.svg_admin_icon, 0)
                }
            }

            if (userInfo.profImgUrl.isNotEmpty()) {
                val imgViewProfPicture = navView.getHeaderView(0).findViewById<ImageView>(R.id.navDrawerImgViewUserPicture)
                val progressBarProfPicture = navView.getHeaderView(0).findViewById<ProgressBar>(R.id.navDrawerProgressBarProfImgLoading)

                progressBarProfPicture.visibility = View.VISIBLE
                GlideLoader(this@MainActivity).loadUserPicture(
                    userInfo.profImgUrl,
                    imgViewProfPicture
                )
                progressBarProfPicture.visibility = View.GONE
            }
        }

    }

    fun userFcmRegistrationTokenSuccess(token: String) {
        MyFirebaseMessagingService.addTokenToFirestore(token)
        Log.e(Constants.TAG, "New token: $token")
    }
}
