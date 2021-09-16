package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapters.ProductsListAdapter
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.ActivityListProductsBinding
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.utils.Constants

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

            // sets VeilRecyclerView's properties
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
                return getString(R.string.txt_category_vegetables)
            "Fruits" ->
                return getString(R.string.txt_category_Fruits)
            "Liquor" ->
                return getString(R.string.txt_category_Liquor)
            "Pharmacy" ->
                return getString(R.string.txt_category_Pharmacy)
            "Household" ->
                return getString(R.string.txt_category_household)
            "Homeware" ->
                return getString(R.string.txt_category_homeware)
            "Grocery" ->
                return getString(R.string.txt_category_grocery)
            "Meat" ->
                return getString(R.string.txt_category_meat)
            "Frozen" ->
                return getString(R.string.txt_category_frozen)
            "Chilled" ->
                return getString(R.string.txt_category_chilled)
            "Fish" ->
                return getString(R.string.txt_category_fish)
            "Beverages" ->
                return getString(R.string.txt_category_beverages)
            "Deals" ->
                return getString(R.string.txt_deals)
            "Popular" ->
                return getString(R.string.txt_popular)
        }
        return filter
    }

    private fun setupUI() {
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionBarLabel.text = getFilterTranslated(filter)
        }
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }
}
