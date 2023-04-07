package com.unipi.mpsp21043.emarket.ui.activities

import android.os.Bundle
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.databinding.ActivityListSettingsBinding
import com.unipi.mpsp21043.emarket.utils.IntentUtils

class ListSettingsActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityListSettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityListSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        setupUI()
    }

    private fun setupUI() {
        setUpActionBar()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonChangePassword.setOnClickListener{ IntentUtils().goToAuthenticateActivity(this@ListSettingsActivity) }
            buttonAddresses.setOnClickListener{ IntentUtils().goToListAddressesActivity(this@ListSettingsActivity) }
            switchButtonNightMode.setOnClickListener{  }
            buttonLanguage.setOnClickListener{  }
            switchButtonFavorites.setOnClickListener{  }
            switchButtonOrders.setOnClickListener{  }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionBarLabel.text = ""
        }
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }
}
