package com.unipi.mpsp21043.admin.ui.activities

import android.app.SearchManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.Query
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.adapters.ProductsListAdapter
import com.unipi.mpsp21043.admin.adapters.ViewPagerMainAdapter
import com.unipi.mpsp21043.admin.database.FirestoreHelper
import com.unipi.mpsp21043.admin.databinding.ActivityMainBinding
import com.unipi.mpsp21043.admin.models.Product
import com.unipi.mpsp21043.admin.models.User
import com.unipi.mpsp21043.admin.ui.fragments.OrdersFragment
import com.unipi.mpsp21043.admin.ui.fragments.ProductsFragment
import com.unipi.mpsp21043.admin.ui.fragments.UsersFragment
import com.unipi.mpsp21043.admin.utils.Constants
import com.unipi.mpsp21043.admin.utils.DialogUtils
import com.unipi.mpsp21043.admin.utils.GlideLoader
import com.unipi.mpsp21043.admin.utils.IntentUtils
import com.unipi.mpsp21043.admin.utils.SnackBarErrorClass
import com.unipi.mpsp21043.admin.utils.SnackBarSuccessClass
import com.unipi.mpsp21043.admin.utils.snackBarErrorClass
import com.unipi.mpsp21043.admin.utils.snackBarSuccessClass


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var sortingOrder: Query.Direction = Query.Direction.DESCENDING
    private lateinit var searchView: SearchView

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
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
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
            toggle.setHomeAsUpIndicator(R.drawable.svg_list)
            toggle.syncState()

            navView.inflateHeaderView(R.layout.nav_drawer_header_signed_in)
            FirestoreHelper().getUserDetails(this@MainActivity)
            navView.setNavigationItemSelectedListener(this@MainActivity)
            navView.setCheckedItem(R.id.nav_drawer_item_products)
        }
    }

    private fun init() {
        // Handle Back Button Press.
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (intent.hasExtra(Constants.EXTRA_SNACKBAR_TYPE)) {
            if (intent.getStringExtra(Constants.EXTRA_SNACKBAR_TYPE) == Constants.STATUS_SUCCESS) {
                snackBarSuccessClass(binding.root, intent.getStringExtra(Constants.EXTRA_SNACKBAR_MESSAGE)!!)
            }
            else if (intent.getStringExtra(Constants.EXTRA_SNACKBAR_TYPE) == Constants.STATUS_ERROR) {
                snackBarErrorClass(binding.root, intent.getStringExtra(Constants.EXTRA_SNACKBAR_MESSAGE)!!)
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
                            toolbar.textViewActionLabel.text = getString(R.string.text_products)
                            navView.setCheckedItem(R.id.nav_drawer_item_products)
                        }
                        1 -> {
                            toolbar.textViewActionLabel.text = getString(R.string.text_orders)
                            navView.setCheckedItem(R.id.nav_drawer_item_orders)
                        }
                        2 -> {
                            toolbar.textViewActionLabel.text = getString(R.string.text_users)
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

    private fun showSelectSortProductsDialog() {

        val productsFragment: ProductsFragment = getVisibleFragment() as ProductsFragment

        binding.apply {
            val dialog = DialogUtils().showDialogSelectSortProducts(this@MainActivity)
            dialog.show()

            val arrayAdapter = ArrayAdapter(this@MainActivity,
                R.layout.dropdown_item_sorting,
                resources.getStringArray(R.array.text_array_sorting_order)
            )
            val dialogDropdownBox = dialog.findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_sort_products_order)
            dialogDropdownBox.setAdapter(arrayAdapter)

            dialogDropdownBox.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    // val selectedValue = arrayAdapter.getItem(position)
                    if (arrayAdapter.getItemId(position).toString() == "0")
                        sortingOrder = Query.Direction.ASCENDING
                    else if (arrayAdapter.getItemId(position).toString() == "1")
                        sortingOrder = Query.Direction.DESCENDING
                }

            val dialogSelectButton = dialog.findViewById<MaterialButton>(R.id.button_dialog_select_sort_products)
            dialogSelectButton.setOnClickListener {
                /* Name */
                if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_name).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_NAME, sortingOrder)

                /* Category */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_category).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_CATEGORY, sortingOrder)

                /* Date Added */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_date_added).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_DATE_ADDED, sortingOrder)

                /* Category */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_category).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_CATEGORY, sortingOrder)

                /* Popularity */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_popularity).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_POPULARITY, sortingOrder)

                /* Price */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_price).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_PRICE, sortingOrder)

                /* Stock */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_stock).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_STOCK, sortingOrder)

                /* Sale */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_products_sale).isChecked)
                    FirestoreHelper()
                        .getProductsList(productsFragment, Constants.FIELD_SALE, sortingOrder)

                dialog.dismiss()
            }
        }
    }

    private fun showSelectSortOrdersDialog() {

        val ordersFragment: OrdersFragment = getVisibleFragment() as OrdersFragment

        binding.apply {
            val dialog = DialogUtils().showDialogSelectSortOrders(this@MainActivity)
            dialog.show()

            val arrayAdapter = ArrayAdapter(this@MainActivity,
                R.layout.dropdown_item_sorting,
                resources.getStringArray(R.array.text_array_sorting_order)
            )
            val dialogDropdownBox = dialog.findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_sort_orders)
            dialogDropdownBox.setAdapter(arrayAdapter)

            dialogDropdownBox.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    // val selectedValue = arrayAdapter.getItem(position)
                    if (arrayAdapter.getItemId(position).toString() == "0")
                        sortingOrder = Query.Direction.ASCENDING
                    else if (arrayAdapter.getItemId(position).toString() == "1")
                        sortingOrder = Query.Direction.DESCENDING
                }

            val dialogSelectButton = dialog.findViewById<MaterialButton>(R.id.button_dialog_select_sort_orders)
            dialogSelectButton.setOnClickListener {
                /* Order Number */
                if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_order_number).isChecked)
                    FirestoreHelper()
                        .getProductsList(ordersFragment, Constants.FIELD_TITLE, sortingOrder)

                /* Order Date Created */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_order_date_created).isChecked)
                    FirestoreHelper()
                        .getProductsList(ordersFragment, Constants.FIELD_ORDER_DATE, sortingOrder)

                /* Order Status */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_order_status).isChecked)
                    FirestoreHelper()
                        .getProductsList(ordersFragment, Constants.FIELD_ORDER_STATUS, sortingOrder)

                /* Payment Method */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_order_payment_method).isChecked)
                    FirestoreHelper()
                        .getProductsList(ordersFragment, Constants.FIELD_PAYMENT_METHOD, sortingOrder)

                /* Sub Total Amount */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_order_sub_total_amount).isChecked)
                    FirestoreHelper()
                        .getProductsList(ordersFragment, Constants.FIELD_SUB_TOTAL_AMOUNT, sortingOrder)

                /* Total Amount */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_order_total_amount).isChecked)
                    FirestoreHelper()
                        .getProductsList(ordersFragment, Constants.FIELD_TOTAL_AMOUNT, sortingOrder)

                /* User ID */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_order_user_id).isChecked)
                    FirestoreHelper()
                        .getProductsList(ordersFragment, Constants.FIELD_USER_ID, sortingOrder)

                dialog.dismiss()
            }
        }
    }

    private fun showSelectSortUsersDialog() {

        val usersFragment: UsersFragment = getVisibleFragment() as UsersFragment

        binding.apply {
            val dialog = DialogUtils().showDialogSelectSortUsers(this@MainActivity)
            dialog.show()

            val arrayAdapter = ArrayAdapter(this@MainActivity,
                R.layout.dropdown_item_sorting,
                resources.getStringArray(R.array.text_array_sorting_order)
            )
            val dialogDropdownBox = dialog.findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_sort_users)
            dialogDropdownBox.setAdapter(arrayAdapter)

            dialogDropdownBox.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    // val selectedValue = arrayAdapter.getItem(position)
                    if (arrayAdapter.getItemId(position).toString() == "0")
                        sortingOrder = Query.Direction.ASCENDING
                    else if (arrayAdapter.getItemId(position).toString() == "1")
                        sortingOrder = Query.Direction.DESCENDING
                }

            val dialogSelectButton = dialog.findViewById<MaterialButton>(R.id.button_dialog_select_sort_users)
            dialogSelectButton.setOnClickListener {
                /* Order Number */
                if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_users_full_name).isChecked)
                    FirestoreHelper()
                        .getProductsList(usersFragment, Constants.FIELD_FULL_NAME, sortingOrder)

                /* Email */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_users_email).isChecked)
                    FirestoreHelper()
                        .getProductsList(usersFragment, Constants.FIELD_EMAIL, sortingOrder)

                /* Phone Number Code */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_users_phone_code).isChecked)
                    FirestoreHelper()
                        .getProductsList(usersFragment, Constants.FIELD_PHONE_CODE, sortingOrder)

                /* Phone Number */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_users_phone).isChecked)
                    FirestoreHelper()
                        .getProductsList(usersFragment, Constants.FIELD_PHONE_NUMBER, sortingOrder)

                /* Notifications */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_users_notifications).isChecked)
                    FirestoreHelper()
                        .getProductsList(usersFragment, Constants.FIELD_NOTIFICATIONS, sortingOrder)

                /* Role */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_users_role).isChecked)
                    FirestoreHelper()
                        .getProductsList(usersFragment, Constants.FIELD_ROLE, sortingOrder)

                /* Profile Completed */
                else if (dialog.findViewById<RadioButton>(R.id.radio_button_sort_users_profile_completed).isChecked)
                    FirestoreHelper()
                        .getProductsList(usersFragment, Constants.FIELD_COMPLETE_PROFILE, sortingOrder)

                dialog.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.action_bar_overflow_menu, menu)

        val searchItem = menu!!.findItem(R.id.action_bar_overflow_search)
        searchView = searchItem.actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager
            .getSearchableInfo(componentName))
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.text_search_something)

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(object : OnQueryTextListener, SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.apply {
                    tabLayout.selectedTabPosition.let {
                        when (it) {
                            0 -> {
                                val fragment: ProductsFragment = getVisibleFragment() as ProductsFragment
                                fragment.productsListAdapter.filter.filter(query)
                            }
                            1 -> {
                                val fragment: ProductsFragment = getVisibleFragment() as ProductsFragment
                                fragment.productsListAdapter.filter.filter(query)
                            }
                            2 -> {
                                val fragment: ProductsFragment = getVisibleFragment() as ProductsFragment
                                fragment.productsListAdapter.filter.filter(query)
                            }
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                binding.apply {
                    tabLayout.selectedTabPosition.let {
                        when (it) {
                            0 -> {
                                val fragment: ProductsFragment = getVisibleFragment() as ProductsFragment
                                fragment.productsListAdapter.filter.filter(newText)
                            }
                            1 -> {
                                val fragment: OrdersFragment = getVisibleFragment() as OrdersFragment
                                fragment.ordersListAdapter.filter.filter(newText)
                            }
                            2 -> {
                                val fragment: UsersFragment = getVisibleFragment() as UsersFragment
                                fragment.usersListAdapter.filter.filter(newText)
                            }
                        }
                    }
                }
                return true
            }
        })

        return false
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_bar_overflow_sort -> {
            binding.apply {
                tabLayout.selectedTabPosition.let {
                    when (it) {
                        0 -> showSelectSortProductsDialog()
                        1 -> showSelectSortOrdersDialog()
                        2 -> showSelectSortUsersDialog()
                    }
                }
            }
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun getVisibleFragment(): Fragment? {
        val fragmentManager: FragmentManager = this@MainActivity.supportFragmentManager
        val fragments: List<Fragment> = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.isVisible) return fragment
        }
        return null
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!searchView.isIconified) {
                searchView.isIconified = true
                return
            }
            ActivityCompat.finishAffinity(this@MainActivity)
        }
    }
}
