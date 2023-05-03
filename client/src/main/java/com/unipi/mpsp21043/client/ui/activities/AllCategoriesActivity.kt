package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.adapters.CategoriesListAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityAllCategoriesBinding
import com.unipi.mpsp21043.client.models.Category

class AllCategoriesActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityAllCategoriesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityAllCategoriesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        setupUI()
        getCategories()
    }

    private fun setupUI() {
        setUpActionBar()
    }

    private fun getCategories() {
        showShimmerUI()
        FirestoreHelper().getCategoriesList(this)
    }

    fun successCategoriesListFromFirestore(categoriesList: ArrayList<Category>) {

        if (categoriesList.size > 0) {
            // Show the recycler and remove the empty state layout completely.
            hideShimmerUI()

            // Sets RecyclerView's properties
            binding.recyclerView.run {
                adapter = CategoriesListAdapter(
                    this@AllCategoriesActivity,
                    categoriesList
                )
                layoutManager = LinearLayoutManager(this@AllCategoriesActivity, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
            }
        }
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutErrorState.root.visibility = View.GONE
            recyclerView.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            recyclerView.visibility = View.VISIBLE
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

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionLabel.text = getString(R.string.text_all_categories)
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
}
