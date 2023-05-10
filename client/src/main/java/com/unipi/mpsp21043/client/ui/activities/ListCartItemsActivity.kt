package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.window.OnBackInvokedDispatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.adapters.CartItemsListAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityListCartItemsBinding
import com.unipi.mpsp21043.client.models.Cart
import com.unipi.mpsp21043.client.models.Product
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.animationSlideUp
import com.unipi.mpsp21043.client.utils.showMustSignInUI
import com.unipi.mpsp21043.client.utils.snackBarErrorClass


class ListCartItemsActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityListCartItemsBinding
    // A global variable for the product list.
    private lateinit var mProductsList: ArrayList<Product>
    // A global variable for the cart list items.
    private lateinit var mCartListItems: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityListCartItemsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
    }

    private fun init() {
        if (FirestoreHelper().getCurrentUserID() != "")
            getProductList()
        else
            showMustSignInUI(this, binding)
        setupUI()
    }
    
    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {
        showShimmerUI()

        FirestoreHelper().getAllProductsList(this)
    }

    /**
     * A function to get the success result of product list.
     *
     * @param productsList
     */
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        mProductsList = productsList

        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreHelper().getCartItemsList(this)
    }

    fun successCartItemsListFromFireStore(cartItemsList: ArrayList<Cart>) {

        hideShimmerUI()

        for (product in mProductsList) {
            for (cart in cartItemsList) {
                if (product.id == cart.productId) {

                    cart.stock = product.stock

                    if (product.stock == 0) {
                        cart.cartQuantity = product.stock
                    }
                }
            }
        }

        mCartListItems = cartItemsList

        if (mCartListItems.size > 0) {

            var subTotal = 0.00

            for (item in mCartListItems) {

                val availableQuantity = item.stock

                if (availableQuantity > 0) {
                    var price = item.price
                    if (item.sale != 0.0)
                        price -= price * item.sale
                    val quantity = item.cartQuantity

                    subTotal += (price * quantity)
                }
            }

            // Show the recycler and remove the empty state layout completely.
            binding.apply {
                layoutEmptyState.root.visibility = View.GONE
                // Costs
                txtViewSubtotalValue.text = String.format(getString(R.string.text_format_price), getString(R.string.curr_eur), subTotal)
                txtViewDeliveryChargeValue.text = String.format(getString(R.string.text_format_price), getString(R.string.curr_eur), Constants.DEFAULT_DELIVERY_COST)
                txtViewTotalAmountValue.text = String.format(getString(R.string.text_format_price), getString(R.string.curr_eur), subTotal + Constants.DEFAULT_DELIVERY_COST)

                // Sets RecyclerView's properties
                recyclerViewItems.run {
                    adapter = CartItemsListAdapter(
                        this@ListCartItemsActivity,
                        cartItemsList,
                        binding
                    )
                    layoutManager = LinearLayoutManager(this@ListCartItemsActivity, LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)
                }
            }
        }
        else {
            // Hide the recycler and show the empty state layout.
            showEmptyStateUI()
        }
    }

    private fun showShimmerUI() {
        binding.apply {
            constraintLayoutAddToCart.visibility = View.INVISIBLE
            layoutEmptyState.root.visibility = View.GONE
            recyclerViewItems.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            constraintLayoutAddToCart.apply {
                if (visibility == View.INVISIBLE) {
                    visibility = View.VISIBLE
                    val animation = animationSlideUp(this)
                    startAnimation(animation)
                }
            }
            layoutEmptyState.root.visibility = View.GONE
            recyclerViewItems.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    fun showEmptyStateUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.VISIBLE
            recyclerViewItems.visibility = View.GONE
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

    /**
     * A function to notify the user about the item removed from the cart list.
     */
    fun itemRemovedSuccess() {
        getCartItemsList()
    }

    /**
     * A function to notify the user about the item quantity updated in the cart list.
     */
    fun itemUpdateSuccess() {
        getCartItemsList()
    }

    fun showErrorItemUpdate() {
        snackBarErrorClass(binding.root, getString(R.string.text_error_item_update_cart), binding.constraintLayoutAddToCart)
    }

    private fun setupUI() {
        binding.apply {
            txtViewSubtotalValue.text = ""
            txtViewDeliveryChargeValue.text = ""
            txtViewTotalAmountValue.text = ""
        }
        setupClickListeners()
        setupActionBar()
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonCheckout.setOnClickListener { IntentUtils().goToListAddressesActivity(this@ListCartItemsActivity, true) }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.actionBarWithToolbar.toolbar)

        val actionBar = supportActionBar
        binding.actionBarWithToolbar.apply {
            textViewActionLabel.text = getString(R.string.text_my_cart)
        }

        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
