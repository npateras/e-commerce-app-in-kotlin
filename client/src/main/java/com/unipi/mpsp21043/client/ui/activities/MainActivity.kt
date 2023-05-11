package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.adapters.ViewPagerMainAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityMainBinding
import com.unipi.mpsp21043.client.models.Cart
import com.unipi.mpsp21043.client.models.User
import com.unipi.mpsp21043.client.service.FirebaseService
import com.unipi.mpsp21043.client.utils.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    // Variable for splash screen
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable support for Splash Screen API for
        // proper Android 12+ support
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                isLoading
            }
        }

        setupUI()
        init()
    }

    private fun init() {

        if (FirestoreHelper().getCurrentUserID() != "") {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                FirebaseService.token = token
            }
        }

        if (intent.hasExtra(Constants.EXTRA_SHOW_ORDER_PLACED_SNACKBAR)
            && intent.getBooleanExtra(Constants.EXTRA_SHOW_ORDER_PLACED_SNACKBAR, false)) {
            snackBarSuccessLargeClass(binding.root, getString(R.string.text_order_placed_successfully))
        }

        // Splash screen can go away since the tasks are completed
        isLoading = false
    }

    private fun setupUI() {
        setupTabs()
        setupActionBar()
        setupNavDrawer()

        if (FirestoreHelper().getCurrentUserID() != "")
            // Check if user has items in his cart
            FirestoreHelper().getCartItemsList(this@MainActivity)

        binding.toolbar.imageButtonMyCart.setOnClickListener { IntentUtils().goToListCartItemsActivity(this) }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            textViewActionLabel.text = getString(R.string.text_store)
        }
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupNavDrawer() {
        binding.apply {
            val toggle = ActionBarDrawerToggle(
                this@MainActivity, drawerLayout, toolbar.root,
                R.string.nav_drawer_open_menu, R.string.nav_drawer_close_menu
            )
            drawerLayout.addDrawerListener(toggle)
            // Change drawer arrow icon
            toggle.drawerArrowDrawable.color =
                ContextCompat.getColor(this@MainActivity, R.color.colorTabSelected)
            // Set navigation arrow icon
            toggle.setHomeAsUpIndicator(R.drawable.ic_list)
            toggle.syncState()

            if (FirestoreHelper().getCurrentUserID().isEmpty()) {
                navView.inflateHeaderView(R.layout.nav_drawer_header_not_signed_in)
                navView.getHeaderView(0)
                    .findViewById<MaterialButton>(R.id.btn_NavView_Sign_In)
                    .setOnClickListener{ goToSignInActivity(this@MainActivity) }
            }
            else {
                navView.inflateHeaderView(R.layout.nav_drawer_header_signed_in)
                FirestoreHelper().getUserDetails(this@MainActivity)
            }
            navView.setNavigationItemSelectedListener(this@MainActivity)

            // Set home page as default
            navView.setCheckedItem(R.id.nav_drawer_item_products)
        }
    }

    private fun setupTabs() {
        val adapter = ViewPagerMainAdapter(supportFragmentManager, lifecycle)

        binding.apply {
            viewPagerHomeActivity.adapter = adapter

            TabLayoutMediator(tabs, viewPagerHomeActivity) { tab, position ->
                when (position) {
                    0 -> tab.setIcon(R.drawable.ic_food_stand)
                    1 -> tab.setIcon(R.drawable.svg_heart_outline_primary_dark)
                    2 -> tab.setIcon(R.drawable.ic_user_circle)
                }
            }.attach()

            toolbar.apply {
                tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        when (tab.position) {
                            0 -> {
                                textViewActionLabel.text = getString(R.string.text_store)
                                navView.setCheckedItem(R.id.nav_drawer_item_products)
                            }
                            1 -> {
                                textViewActionLabel.text = getString(R.string.text_favorites)
                                navView.setCheckedItem(R.id.nav_drawer_item_favorites)
                            }
                            2 -> {
                                textViewActionLabel.text = getString(R.string.text_my_account)
                                navView.setCheckedItem(R.id.nav_drawer_item_profile)
                            }
                        }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
            }
        }
    }

    private fun refreshActivity() {
        if (FirestoreHelper().getCurrentUserID() != "") {
            FirestoreHelper().getUserDetails(this@MainActivity)
            FirestoreHelper().getCartItemsList(this@MainActivity)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        binding.apply {
            when (item.itemId) {
                R.id.nav_drawer_item_products -> tabs.getTabAt(0)?.select()
                R.id.nav_drawer_item_favorites -> tabs.getTabAt(1)?.select()
                R.id.nav_drawer_item_cart -> {
                    IntentUtils().goToListCartItemsActivity(this@MainActivity)
                    return false
                }
                R.id.nav_drawer_item_profile -> tabs.getTabAt(2)?.select()
                R.id.nav_drawer_item_orders -> {
                    IntentUtils().goToListOrdersActivity(this@MainActivity)
                    return false
                }
                R.id.nav_drawer_item_settings -> {
                    IntentUtils().goToListSettingsActivity(this@MainActivity)
                    return false
                }
                R.id.nav_drawer_item_exit -> ActivityCompat.finishAffinity(this@MainActivity)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_Layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun userDetailsSuccess(userInfo: User) {
        binding.apply {
            navView.getHeaderView(0).apply {
                findViewById<TextView>(R.id.nav_drawer_logged_in_full_name)
                    .text = userInfo.fullName
            }

            if (userInfo.profImgUrl.isNotEmpty()) {
                val imgViewProfPicture = navView.getHeaderView(0).findViewById<ImageView>(R.id.nav_drawer_logged_in_profile_picture)

                GlideLoader(this@MainActivity).loadUserPicture(
                    userInfo.profImgUrl,
                    imgViewProfPicture
                )
            }
        }
    }


    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    fun userCartSuccess(cartItems: ArrayList<Cart>) {
        if (cartItems.isNotEmpty()) {
            createBadge(this@MainActivity, binding.toolbar.imageButtonMyCart, cartItems.size)
        }
    }

    override fun onResume() {
        super.onResume()

        refreshActivity()
    }

}
