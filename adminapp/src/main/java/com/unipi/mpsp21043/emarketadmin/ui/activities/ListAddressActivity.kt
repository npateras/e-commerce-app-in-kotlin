package com.unipi.mpsp21043.emarketadmin.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.adapters.AddressListAdapter
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.ActivityListAddressesBinding
import com.unipi.mpsp21043.emarketadmin.models.Address
import com.unipi.mpsp21043.emarketadmin.utils.Constants
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils
import com.unipi.mpsp21043.emarketadmin.utils.SwipeToDeleteCallback
import com.unipi.mpsp21043.emarketadmin.utils.SwipeToEditCallback

class ListAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityListAddressesBinding
    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityListAddressesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
    }

    private fun init () {
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress =
                intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        setupUI()
        getUserAddresses()
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerViewItems.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerViewItems.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    private fun showEmptyStateUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.VISIBLE
            recyclerViewItems.visibility = View.GONE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {

                getUserAddresses()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled: ", "Add an address.")
        }
    }

    private fun getUserAddresses() {
        FirestoreHelper().getAddressList(this@ListAddressActivity)
    }

    fun successUserAddressListFromFirestore(addressList: ArrayList<Address>) {

        if (addressList.size > 0) {
            // Show the recycler and remove the empty state layout completely.
            hideShimmerUI()

            // Sets RecyclerView's properties
            binding.recyclerViewItems.run {
                adapter = AddressListAdapter(
                    this@ListAddressActivity,
                    addressList
                )
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@ListAddressActivity, LinearLayoutManager.VERTICAL, false)

                if (!mSelectAddress) {
                    val editSwipeHandler = object : SwipeToEditCallback(this@ListAddressActivity) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            val adapter = adapter as AddressListAdapter
                            adapter.notifyEditItem(
                                this@ListAddressActivity,
                                viewHolder.absoluteAdapterPosition
                            )
                        }
                    }
                    val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                    editItemTouchHelper.attachToRecyclerView(this)


                    val deleteSwipeHandler = object : SwipeToDeleteCallback(this@ListAddressActivity) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            // Show the progress dialog.
                            showProgressDialog()

                            FirestoreHelper().deleteAddress(
                                this@ListAddressActivity,
                                addressList[viewHolder.absoluteAdapterPosition].id
                            )
                        }
                    }
                    val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                    deleteItemTouchHelper.attachToRecyclerView(this)
                }
            }
        }
        else
            showEmptyStateUI()
    }

    /**
     * A function notify the user that the address is deleted successfully.
     */
    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this,
            resources.getString(R.string.txt_address_deleted_confirmation),
            Toast.LENGTH_SHORT
        ).show()

        getUserAddresses()
    }

    private fun setupUI() {
        showShimmerUI()

        setUpActionBar()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnAddNewAddress.setOnClickListener{ IntentUtils().goToAddNewAddressActivity(this@ListAddressActivity)}
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionBarLabel.text = getString(R.string.txt_addresses)
        }
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
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
