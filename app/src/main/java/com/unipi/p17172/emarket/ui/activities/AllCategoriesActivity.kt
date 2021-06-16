package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.ActivityAllCategoriesBinding

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
        FirestoreHelper()
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