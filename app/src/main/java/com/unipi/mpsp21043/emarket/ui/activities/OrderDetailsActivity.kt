package com.unipi.mpsp21043.emarket.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.adapters.OrderCartProductListAdapter
import com.unipi.mpsp21043.emarket.databinding.ActivityOrderDetailsBinding
import com.unipi.mpsp21043.emarket.models.Order
import com.unipi.mpsp21043.emarket.utils.Constants

class OrderDetailsActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityOrderDetailsBinding

    // A global variable for Order details.
    private lateinit var mOrderDetails: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
        setupUI()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_ORDER_DETAILS)) {
            mOrderDetails  = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(Constants.EXTRA_ORDER_DETAILS, Order::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Constants.EXTRA_ORDER_DETAILS)!!
            }

            setRecyclerView()
        }
    }

    private fun setRecyclerView() {
        // sets VeilRecyclerView's properties
        binding.veilRecyclerView.run {
            setVeilLayout(R.layout.shimmer_item_product)
            setAdapter(
                OrderCartProductListAdapter(
                    this@OrderDetailsActivity,
                    mOrderDetails.cartItems
                )
            )
            setLayoutManager(LinearLayoutManager(this@OrderDetailsActivity, LinearLayoutManager.VERTICAL, false))
            getRecyclerView().setHasFixedSize(false)
        }
    }

    private fun setupUI() {
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionBarLabel.text = Constants.standardSimpleDateFormat.format(mOrderDetails.orderDate)
        }
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }
}
