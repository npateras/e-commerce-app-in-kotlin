package com.unipi.mpsp21043.emarketadmin.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.adapters.SearchListAdapter
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.ActivitySearchBinding
import com.unipi.mpsp21043.emarketadmin.models.Search


class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }


    /**
     * A function to initialize the required properties of the view.
     *
     */
    private fun init() {
        setupUI()
    }

    /**
     * A function to setup the required properties for the UI.
     *
     */
    private fun setupUI() {
        setupActionBar()
        setupClickListeners()
    }

    /**
     * A function to setup the required click listeners.
     *
     */
    private fun setupClickListeners() {
        binding.apply {
            toolbar.actionBarButtonSearch.setOnClickListener { searchFirestore() }
        }
    }

    /**
     * A function to show & hide the required views based on the results.
     *
     */
    private fun showLoadingDetailsUI() {
        binding.apply {
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
            layoutStateSearch.root.visibility = View.GONE
            layoutEmptyStateSearchFailed.root.visibility = View.GONE
            recyclerViewItems.visibility = View.GONE
        }
    }

    /**
     * A function to show & hide the required views based on the results.
     *
     */
    private fun showResultsUI() {
        binding.apply {
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
            layoutStateSearch.root.visibility = View.GONE
            layoutEmptyStateSearchFailed.root.visibility = View.GONE
            recyclerViewItems.visibility = View.VISIBLE
        }
    }

    /**
     * A function to show & hide the required views based on the results.
     *
     */
    private fun showNoResultsFoundUI() {
        binding.apply {
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
            layoutStateSearch.root.visibility = View.GONE
            layoutEmptyStateSearchFailed.root.visibility = View.VISIBLE
            recyclerViewItems.visibility = View.GONE
        }
    }

    /**
     * A function to search the database for results.
     *
     */
    private fun searchFirestore() {
        // Show & hide the required views while WAITING getting the results.
        showLoadingDetailsUI()

        // Get results from database
        FirestoreHelper().getSearchResults(this@SearchActivity)
    }

    /**
     * A function to setup the required properties for action bar.
     *
     * @param searchList Will receive the items list from Cloud Firestore.
     */
    fun searchResultsSuccessFromFirestore(searchList: ArrayList<Search>) {

        if (searchList.size > 0) {
            // Setting RecyclerView's properties
            binding.recyclerViewItems.run {
                adapter = SearchListAdapter(
                    this@SearchActivity,
                    searchList
                )
                hasFixedSize()
                layoutManager =
                    LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            }

            // Show & hide the required views based on the results.
            showResultsUI()
        }
        else
            // Show & hide the required views based on the results.
            showNoResultsFoundUI()

    }

    /**
     * A function to setup the required properties for action bar.
     *
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            // Enabling our toolbar's custom view.
            it.setCustomView(R.layout.toolbar_search)
            // Enabling our toolbar's back button.
            it.setDisplayHomeAsUpEnabled(true)
            // Setting up our toolbar's custom back button icon.
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }

    /**
     * An override function that will be executed when the toolbar back button is pressed.
     *
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * An override function that will be executed when the device's back button is pressed.
     *
     */
    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        // Need this to go back to previous activity as it was left.
        finish()
        return super.getOnBackInvokedDispatcher()
    }
}
