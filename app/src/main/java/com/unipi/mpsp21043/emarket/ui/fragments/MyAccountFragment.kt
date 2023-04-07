package com.unipi.mpsp21043.emarket.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.databinding.FragmentMyAccountBinding
import com.unipi.mpsp21043.emarket.models.User
import com.unipi.mpsp21043.emarket.ui.activities.MainActivity
import com.unipi.mpsp21043.emarket.utils.Constants
import com.unipi.mpsp21043.emarket.utils.GlideLoader
import com.unipi.mpsp21043.emarket.utils.IntentUtils

class MyAccountFragment : BaseFragment() {
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentMyAccountBinding? = null
    private lateinit var mUserDetails: User
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)

        init()
        setupUI()

        return binding.root
    }

    /**
     * A function to execute all the initializations needed.
     */
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
        // If user is signed in
        if (FirestoreHelper().getCurrentUserID() != "")
            userLoggedInUI()
        // If user is NOT signed in
        else
            userNotLoggedInUI()
    }

    private fun userNotLoggedInUI() {
        binding.apply {
            // We make the sign in layout visible and add the button click listeners accordingly.
            layoutMustBeSignedIn.apply {
                root.visibility = View.VISIBLE
                btnSignIn.setOnClickListener{ goToSignInActivity(this@MyAccountFragment.requireContext()) }
                txtViewSignUp.setOnClickListener{ goToSignInActivity(this@MyAccountFragment.requireContext()) }
            }
        }
    }

    private fun userLoggedInUI() {
        binding.apply {
            btnUpdateProfile.setOnClickListener { IntentUtils().goToUpdateUserDetailsActivity(this@MyAccountFragment, mUserDetails) }
            btnAddresses.setOnClickListener { IntentUtils().goToListAddressesActivity(this@MyAccountFragment) }
            btnLogOut.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                IntentUtils().createNewMainActivity(this@MyAccountFragment.requireActivity())
            }
        }
    }

    /**
     * A function to get the user details.
     */
    private fun getUserDetails() {
        FirestoreHelper().getUserDetails(this@MyAccountFragment)
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
                    textViewPhoneValue.text = String.format(getString(R.string.text_format_phone), mUserDetails.phoneCode, mUserDetails.phoneNumber)
                else
                    textViewPhoneValue.text = mUserDetails.phoneNumber
            else textViewPhoneValue.text = getString(R.string.text_none)

            if (mUserDetails.profImgUrl.isNotEmpty()) {
                GlideLoader(this@MyAccountFragment.requireContext()).loadUserPicture(
                    mUserDetails.profImgUrl,
                    imgViewUserPicture
                )
            }
        }

        hideShimmerUI()
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutStateError.root.visibility = View.GONE
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
            layoutStateError.root.visibility = View.VISIBLE
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmer()
        }
    }

    /**
     * We clean up any references to the binding class instance in the fragment's onDestroyView() method.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
