package com.unipi.mpsp21043.client.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityListSettingsBinding
import com.unipi.mpsp21043.client.utils.IntentUtils

class ListSettingsActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityListSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences =


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

        if (FirestoreHelper().getCurrentUserID() != "")
            setupClickListenersLoggedIn()
        else
            hideNotLoggedInContent()
    }

    private fun setupClickListenersLoggedIn() {
        binding.apply {
            buttonChangePassword.setOnClickListener { IntentUtils().goToAuthenticateActivity(this@ListSettingsActivity) }
            buttonAddresses.setOnClickListener { IntentUtils().goToListAddressesActivity(this@ListSettingsActivity) }
            switchButtonFavorites.setOnClickListener {  }
            switchButtonOrders.setOnClickListener {  }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            switchButtonNightMode.setOnClickListener {  }
            buttonLanguage.setOnClickListener {  }
        }
    }

    private fun hideNotLoggedInContent() {
        binding.apply {
            textViewAccount.visibility = View.GONE
            textViewNotifications.visibility = View.GONE
            switchButtonFavorites.visibility = View.GONE
            switchButtonOrders.visibility = View.GONE
            buttonAddresses.visibility = View.GONE
            buttonChangePassword.visibility = View.GONE
            buttonSignOut.visibility = View.GONE
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionLabel.text = getString(R.string.text_settings)
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
