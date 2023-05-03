package com.unipi.mpsp21043.admin.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.database.FirestoreHelper
import com.unipi.mpsp21043.admin.databinding.ActivityMyAccountBinding
import com.unipi.mpsp21043.admin.models.User
import com.unipi.mpsp21043.admin.utils.Constants
import com.unipi.mpsp21043.admin.utils.GlideLoader
import com.unipi.mpsp21043.admin.utils.IntentUtils

class MyAccountActivity : BaseActivity() {

    private lateinit var binding: ActivityMyAccountBinding

    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityMyAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
        setupUI()
    }

    private fun init() {
        showShimmerUI()

        // Check if the user is logged in, otherwise show the sign in state.
        if (FirestoreHelper().getCurrentUserID() != "") {
            // Apply click listeners


            // GET user details
            getUserDetails()
        }
    }

    /**
     * A function to setup all the UI requirements to be viewable-ready.
     */
    private fun setupUI() {
        setupActionBar()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonUpdate.setOnClickListener { IntentUtils().goToUpdateUserDetailsActivity(this@MyAccountActivity, mUserDetails) }
            buttonSignOut.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                IntentUtils().goToSignInActivity(this@MyAccountActivity)
            }
        }
    }

    /**
     * A function to get the user details.
     */
    private fun getUserDetails() {
        FirestoreHelper().getUserDetails(this@MyAccountActivity)
    }

    /**
     * A function to notify the success result of user details.
     *
     * @param mUser A model class with user details.
     */
    fun userDetailsSuccess(mUser: User) {
        mUserDetails = mUser
        setupUI()

        // Set user details.
        binding.apply {
            textViewFullName.text = mUserDetails.fullName
            textViewEmailValue.text = mUserDetails.email
            textViewDateRegisteredValue.text = Constants.standardSimpleDateFormat.format(mUserDetails.dateRegistered)

            // If some details aren't set by the user we completely remove the view instead of
            // showing a blank view.
            if (mUserDetails.phoneNumber != "")
                if (mUserDetails.phoneCode.toString() != "")
                    textViewPhoneNumberValue.text = String.format(getString(R.string.text_format_phone), mUserDetails.phoneCode, mUserDetails.phoneNumber)
                else
                    textViewPhoneNumberValue.text = mUserDetails.phoneNumber
            else textViewPhoneNumberValue.text = getString(R.string.text_none)

            if (mUserDetails.profImgUrl.isNotEmpty()) {
                GlideLoader(this@MyAccountActivity).loadUserPicture(
                    mUserDetails.profImgUrl,
                    circleImageViewUserPicture
                )
            }
        }

        hideShimmerUI()
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutErrorState.root.visibility = View.GONE
            constraintLayoutContainer.visibility = View.GONE
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            constraintLayoutContainer.visibility = View.VISIBLE
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmer()
        }
    }

    fun showErrorUI() {
        binding.apply {
            layoutErrorState.root.visibility = View.VISIBLE
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmer()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionLabel.text = getString(R.string.text_my_account)
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
