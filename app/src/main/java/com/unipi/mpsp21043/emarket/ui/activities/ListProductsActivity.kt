package com.unipi.mpsp21043.emarket.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.adapters.ProductsListAdapter
import com.unipi.mpsp21043.emarket.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.databinding.ActivityListProductsBinding
import com.unipi.mpsp21043.emarket.models.Product
import com.unipi.mpsp21043.emarket.utils.Constants

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
        FirestoreHelper().getProductsListFromCategory(this@ListProductsActivity, filter)
    }

    fun successOProductsListFromFirestore(productsList: ArrayList<Product>) {

        if (productsList.size > 0) {
            // Show the recycler and remove the empty state layout completely.
            binding.apply {
                veilRecyclerView.visibility = View.VISIBLE
                layoutEmptyState.root.visibility = View.GONE
            }

            // Sets RecyclerView's properties
            binding.veilRecyclerView.run {
                setVeilLayout(R.layout.shimmer_item_product)
                setAdapter(ProductsListAdapter(this@ListProductsActivity, productsList))
                setLayoutManager(GridLayoutManager(this@ListProductsActivity, 3, GridLayoutManager.VERTICAL, false))
                getRecyclerView().setHasFixedSize(true)
                addVeiledItems(7)
                // delay-auto-unveil
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        unVeil()
                    },
                    1000
                )
            }
        }
        else {
            // Hide the recycler and show the empty state layout.
            binding.apply {
                veilRecyclerView.unVeil()
                veilRecyclerView.visibility = View.INVISIBLE
                layoutEmptyState.root.visibility = View.VISIBLE
            }

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
            textViewActionBarLabel.text = getFilterTranslated(filter)
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }

}
