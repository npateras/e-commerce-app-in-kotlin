package com.unipi.p17172.emarket.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapters.CartItemsListAdapter
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.FragmentMyCartBinding
import com.unipi.p17172.emarket.models.Cart
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.utils.Constants
import com.unipi.p17172.emarket.utils.IntentUtils

class MyCartFragment : BaseFragment() {
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentMyCartBinding? = null
    private val binding get() = _binding!!

    // A global variable for the product list.
    private lateinit var mProductsList: ArrayList<Product>

    // A global variable for the cart list items.
    private lateinit var mCartListItems: ArrayList<Cart>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCartBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        getProductList()
        setupUI()
    }

    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {
        veilRecycler()

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

        binding.veilRecyclerView.unVeil()

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
                    val price = item.price
                    val quantity = item.cartQuantity

                    subTotal += (price * quantity)
                }
            }

            // Show the recycler and remove the empty state layout completely.
            binding.apply {
                veilRecyclerView.visibility = View.VISIBLE
                layoutEmptyState.root.visibility = View.GONE
                // Costs
                txtViewSubtotalValue.text = String.format(getString(R.string.txt_format_price), getString(R.string.curr_eur), subTotal)
                txtViewDeliveryChargeValue.text = String.format(getString(R.string.txt_format_price), getString(R.string.curr_eur), Constants.DEFAULT_DELIVERY_COST)
                txtViewTotalAmountValue.text = String.format(getString(R.string.txt_format_price), getString(R.string.curr_eur), subTotal + Constants.DEFAULT_DELIVERY_COST)
            }

            // sets VeilRecyclerView's properties
            binding.veilRecyclerView.run {
                setVeilLayout(R.layout.shimmer_item_product)
                setAdapter(
                    CartItemsListAdapter(
                        requireContext(),
                        cartItemsList,
                        true
                    )
                )
                setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false))
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
        } else {
            // Hide the recycler and show the empty state layout.
            binding.apply {
                veilRecyclerView.visibility = View.INVISIBLE
                layoutEmptyState.root.visibility = View.VISIBLE
            }

        }
    }

    private fun veilRecycler() {
        binding.apply {
            veilRecyclerView.veil()
            veilRecyclerView.addVeiledItems(Constants.DEFAULT_VEILED_ITEMS_HORIZONTAL)
        }
    }

    /**
     * A function to notify the user about the item removed from the cart list.
     */
    fun itemRemovedSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this.requireContext(),
            resources.getString(R.string.txt_item_removed_from_cart),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    /**
     * A function to notify the user about the item quantity updated in the cart list.
     */
    fun itemUpdateSuccess() {

        hideProgressDialog()

        getCartItemsList()
    }

    private fun setupUI() {
        binding.apply {
            txtViewSubtotalValue.text = ""
            txtViewDeliveryChargeValue.text = ""
            txtViewTotalAmountValue.text = ""
        }
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnCheckout.setOnClickListener { IntentUtils().goToCheckoutActivity(requireActivity()) }
        }
    }

    override fun onResume() {
        super.onResume()

        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
