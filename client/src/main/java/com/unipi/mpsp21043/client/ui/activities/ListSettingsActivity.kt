package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityListSettingsBinding
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.showMustSignInUI


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

        if (FirestoreHelper().getCurrentUserID() != "")
            setupClickListeners()
        else
            showMustSignInUI(this, binding)
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonChangePassword.setOnClickListener { IntentUtils().goToAuthenticateActivity(this@ListSettingsActivity) }
            buttonAddresses.setOnClickListener { IntentUtils().goToListAddressesActivity(this@ListSettingsActivity) }
            switchButtonFavorites.apply {
                /*setOnClickListener {
                    FirestoreHelper().updateUserSettingsNotifications("type", true)
                }*/
                setOnCheckedChangeListener { _, isChecked ->
                    // FirestoreHelper().updateUserSettingsNotifications("type", isChecked)
                }
            }
            switchButtonOrders.setOnClickListener {

            }
        }
    }

    private fun setUpActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(toolbar)
            textViewActionLabel.text = getString(R.string.text_settings)
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
