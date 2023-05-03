package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.recyclerview.widget.GridLayoutManager
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.adapters.ProductsListAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityListProductsBinding
import com.unipi.mpsp21043.client.models.Product
import com.unipi.mpsp21043.client.utils.Constants

class ListProductsActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityListProductsBinding
    private var filter = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityListProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
    }

    private fun init () {
        if (intent.hasExtra(Constants.EXTRA_CATEGORY_NAME))
            filter = intent.getStringExtra(Constants.EXTRA_CATEGORY_NAME)!!

        setupUI()
        getProducts()
    }

    private fun getProducts() {
        showShimmerUI()

        FirestoreHelper().getProductsListFromCategory(this@ListProductsActivity, filter)
    }

    fun successOProductsListFromFirestore(productsList: ArrayList<Product>) {

        if (productsList.size > 0) {
            // Show the recycler and remove the empty state layout completely.
            hideShimmerUI()

            // Sets RecyclerView's properties
            binding.recyclerView.run {
                adapter = ProductsListAdapter(this@ListProductsActivity, productsList)
                layoutManager = GridLayoutManager(this@ListProductsActivity, 3, GridLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
            }
        }
        else {
            // Hide the recycler and show the empty state layout.
            showEmptyStateUI()
        }
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerView.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    private fun showEmptyStateUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    fun showErrorUI() {
        hideShimmerUI()
        binding.apply {
            layoutErrorState.root.visibility = View.VISIBLE
        }
    }

    private fun getFilterTranslated(filter: String): String {
        when (filter) {
            "Vegetables" ->
                return getString(R.string.text_category_vegetables)
            "Fruits" ->
                return getString(R.string.text_category_fruits)
            "Liquor" ->
                return getString(R.string.text_category_liquor)
            "Pharmacy" ->
                return getString(R.string.text_category_pharmacy)
            "Household" ->
                return getString(R.string.text_category_household)
            "Homeware" ->
                return getString(R.string.text_category_homeware)
            "Grocery" ->
                return getString(R.string.text_category_grocery)
            "Meat" ->
                return getString(R.string.text_category_meat)
            "Frozen" ->
                return getString(R.string.text_category_frozen)
            "Chilled" ->
                return getString(R.string.text_category_chilled)
            "Fish" ->
                return getString(R.string.text_category_fish)
            "Beverages" ->
                return getString(R.string.text_category_beverages)
            "Deals" ->
                return getString(R.string.text_deals)
            "Popular" ->
                return getString(R.string.text_popular)
        }
        return filter
    }

    private fun setupUI() {
        setupActionBar()
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            textViewActionLabel.text = getFilterTranslated(filter)
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        finish()
        return super.getOnBackInvokedDispatcher()
    }
}
