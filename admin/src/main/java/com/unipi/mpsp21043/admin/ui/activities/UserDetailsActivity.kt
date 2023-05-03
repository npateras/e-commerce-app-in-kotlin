package com.unipi.mpsp21043.admin.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.database.FirestoreHelper
import com.unipi.mpsp21043.admin.databinding.ActivityUserDetailsBinding
import com.unipi.mpsp21043.admin.models.User
import com.unipi.mpsp21043.admin.utils.Constants
import com.unipi.mpsp21043.admin.utils.GlideLoader

class UserDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var mUserDetails: User
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    /**
     * A function to execute all the initializations needed.
     */
    private fun init() {
        setupUI()

        if (intent.hasExtra(Constants.EXTRA_USER_ID)) {
            // Get the user details from intent as a ParcelableExtra.
            userId = intent.getStringExtra(Constants.EXTRA_USER_ID)!!
        }

        getUserDetails()
    }

    private fun showShimmerUI() {
        binding.apply {
            scrollViewBody.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            scrollViewBody.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    /**
     * A function to setup all the UI requirements to be viewable-ready.
     */
    private fun setupUI() {
        setupActionBar()
        showShimmerUI()
    }

    /**
     * A function to get the user details.
     */
    private fun getUserDetails() {
        FirestoreHelper().getUserDetails(this@UserDetailsActivity)
    }

    /**
     * A function to notify the success result of user details.
     *
     * @param mUser A model class with user details.
     */
    fun userDetailsSuccess(mUser: User) {
        mUserDetails = mUser

        // Set user details.
        binding.apply {
            toolbar.textViewActionLabel.text = String.format(
                getString(R.string.text_format_user_details),
                mUserDetails.fullName
            )

            textViewFullName.text = mUserDetails.fullName
            textViewEmailValue.text = mUserDetails.email
            textViewDateRegisteredValue.text = Constants.standardSimpleDateFormat.format(mUserDetails.dateRegistered)

            // If some details aren't set by the user we completely remove the view instead of
            // showing a blank view.
            if (mUserDetails.profImgUrl.isNotEmpty()) {
                GlideLoader(this@UserDetailsActivity).loadUserPicture(
                    mUserDetails.profImgUrl,
                    binding.circleImageViewUserPicture
                )
            }

            if (mUserDetails.phoneNumber.isNotEmpty())
                if (mUserDetails.phoneCode.toString().isNotEmpty())
                    textViewPhoneValue.text = String.format(getString(R.string.text_format_phone), mUserDetails.phoneCode, mUserDetails.phoneNumber)
                else
                    textViewPhoneValue.text = mUserDetails.phoneNumber
            else textViewPhoneValue.text = getString(R.string.text_none)

            if (mUserDetails.role.isNotEmpty())
                if (mUserDetails.role == Constants.ROLE_USER)
                    textViewRoleValue.text = getString(R.string.text_user)
                else if (mUserDetails.role == Constants.ROLE_ADMIN)
                    textViewRoleValue.text = getString(R.string.text_admin)

            if (mUserDetails.notifications)
                textViewNotificationsValue.text = getString(R.string.text_yes)
            else
                textViewNotificationsValue.text = getString(R.string.text_no)

            if (mUserDetails.registrationTokens.isNotEmpty())
                textViewActiveDevicesValue.text = mUserDetails.registrationTokens.size.toString()

            if (mUserDetails.profileCompleted)
                textViewProfileCompletedValue.text = getString(R.string.text_yes)
            else
                textViewProfileCompletedValue.text = getString(R.string.text_no)

        }

        hideShimmerUI()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionLabel.text = getString(R.string.user_details)
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
