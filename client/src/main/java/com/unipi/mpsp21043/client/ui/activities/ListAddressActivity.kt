package com.unipi.mpsp21043.client.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.adapters.AddressListAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityListAddressesBinding
import com.unipi.mpsp21043.client.models.Address
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.SwipeToDeleteCallback
import com.unipi.mpsp21043.client.utils.SwipeToEditCallback
import com.unipi.mpsp21043.client.utils.snackBarErrorClass

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
            Log.e("Request Cancelled", "To add the address.")
        }
    }

    private fun getUserAddresses() {
        showShimmerUI()

        FirestoreHelper().getAddressList(this@ListAddressActivity)
    }

    fun successUserAddressListFromFirestore(addressList: ArrayList<Address>) {

        if (addressList.size > 0) {
            // Show the recycler and remove the empty state layout completely.
            hideShimmerUI()

            // Sets RecyclerView's properties
            binding.recyclerView.run {
                adapter = AddressListAdapter(this@ListAddressActivity, addressList, mSelectAddress)
                layoutManager = LinearLayoutManager(this@ListAddressActivity, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)

                if (!mSelectAddress) {
                    val editSwipeHandler = object : SwipeToEditCallback(this@ListAddressActivity) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            val adapter = this@run.adapter as AddressListAdapter
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

                            showShimmerUI()

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
        else {
            // Hide the recycler and show the empty state layout.
            showEmptyStateUI()
        }
    }

    fun showDeleteAddressError() {
        snackBarErrorClass(binding.root, getString(R.string.text_error_while_deleting_the_address), binding.buttonAddAddress)
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

    /**
     * A function notify the user that the address is deleted successfully.
     */
    fun deleteAddressSuccess() {

        snackBarErrorClass(binding.root, getString(R.string.text_address_deleted_confirmation), binding.buttonAddAddress)

        getUserAddresses()
    }

    private fun setupUI() {
        setUpActionBar()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonAddAddress.setOnClickListener{ IntentUtils().goToAddNewAddressActivity(this@ListAddressActivity)}
        }
    }

    private fun setUpActionBar() {
        binding.actionBarWithToolbar.apply {
            setSupportActionBar(toolbar)
            textViewActionLabel.text = getString(R.string.text_addresses)
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
