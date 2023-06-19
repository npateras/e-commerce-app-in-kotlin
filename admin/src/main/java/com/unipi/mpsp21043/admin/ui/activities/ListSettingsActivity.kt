package com.unipi.mpsp21043.admin.ui.activities

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.databinding.ActivityListSettingsBinding
import com.unipi.mpsp21043.admin.utils.IntentUtils


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
            buttonChangePassword.setOnClickListener { IntentUtils().goToAuthenticateActivity(this@ListSettingsActivity) }
            buttonSignOut.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                IntentUtils().goToSignInActivity(this@ListSettingsActivity)
            }
        }
    }

    private fun setUpActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
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
