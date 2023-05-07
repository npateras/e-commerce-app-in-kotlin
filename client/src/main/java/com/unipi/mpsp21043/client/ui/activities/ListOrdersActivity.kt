package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.adapters.OrdersListAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityListOrdersBinding
import com.unipi.mpsp21043.client.models.Order

class ListOrdersActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityListOrdersBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityListOrdersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
        setupUI()
    }

    private fun init() {

        if (FirestoreHelper().getCurrentUserID() != "") {
            getOrdersList()
        }
        else
            com.unipi.mpsp21043.client.utils.showMustSignInUI(this, binding)
    }
    private fun setupUI() {
        setUpActionBar()
    }

    private fun getOrdersList() {
        showShimmerUI()

        FirestoreHelper().getMyOrdersList(this)
    }

    fun successOrdersListFromFirestore(ordersList: ArrayList<Order>) {

        if (ordersList.size > 0) {
            // Show the recycler and remove the empty state layout completely.
            hideShimmerUI()

            // Sets RecyclerView's properties
            binding.recyclerView.run {
                adapter = OrdersListAdapter(
                    this@ListOrdersActivity,
                    ordersList
                )
                hasFixedSize()
                layoutManager = LinearLayoutManager(this@ListOrdersActivity, LinearLayoutManager.VERTICAL, false)
            }
        }
        else
            showEmptyStateUI()
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
        binding.apply {
            layoutErrorState.root.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionLabel.text = getString(R.string.text_my_orders)
        }
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

    override fun onResume() {
        super.onResume()

        getOrdersList()
    }
}
