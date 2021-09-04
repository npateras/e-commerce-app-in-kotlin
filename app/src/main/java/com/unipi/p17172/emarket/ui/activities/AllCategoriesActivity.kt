package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapters.CategoriesListAdapter
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.ActivityAllCategoriesBinding
import com.unipi.p17172.emarket.models.Category

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
        FirestoreHelper().getCategoriesList(this)
    }

    fun successCategoriesListFromFirestore(categoriesList: ArrayList<Category>) {

        if (categoriesList.size > 0) {
            // Show the recycler and remove the empty state layout completely.
            binding.apply {
                veilRecyclerViewAllCategories.visibility = View.VISIBLE
                layoutEmptyStateCategories.root.visibility = View.GONE
            }

            // sets VeilRecyclerView's properties
            binding.veilRecyclerViewAllCategories.run {
                setVeilLayout(R.layout.shimmer_item_product)
                setAdapter(
                    CategoriesListAdapter(
                        this@AllCategoriesActivity,
                        categoriesList
                    )
                )
                setLayoutManager(LinearLayoutManager(this@AllCategoriesActivity, LinearLayoutManager.VERTICAL, false))
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
                veilRecyclerViewAllCategories.unVeil()
                veilRecyclerViewAllCategories.visibility = View.INVISIBLE
                layoutEmptyStateCategories.root.visibility = View.VISIBLE
            }

        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionBarLabel.text = getString(R.string.txt_all_categories)
        }
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }
}
