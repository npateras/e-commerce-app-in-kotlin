package com.unipi.mpsp21043.emarketadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.FragmentMyAccountBinding
import com.unipi.mpsp21043.emarketadmin.models.User
import com.unipi.mpsp21043.emarketadmin.ui.activities.MainActivity
import com.unipi.mpsp21043.emarketadmin.utils.Constants
import com.unipi.mpsp21043.emarketadmin.utils.GlideLoader
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils

class MyAccountFragment : Fragment() {
    // ~~~~~~~VARIABLES~~~~~~~
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentMyAccountBinding? = null
    private lateinit var mUserDetails: User
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    /**
     * A function to execute all the initializations needed.
     */
    private fun init() {
        setupUI()
    }

    /**
     * A function to setup all the UI requirements to be viewable-ready.
     */
    private fun setupUI() {
        // Veil the veiled layouts until we load our data.
        veilDetails()

        // Check if the user is logged in, otherwise show the sign in state.
        if (FirestoreHelper().getCurrentUserID() != "") {
            // Apply click listeners
            binding.apply {
                btnUpdateProfile.setOnClickListener { IntentUtils().goToUpdateUserDetailsActivity(this@MyAccountFragment.requireContext(), mUserDetails) }
                btnAddresses.setOnClickListener { IntentUtils().goToListAddressesActivity(this@MyAccountFragment.requireContext()) }
                btnLogOut.setOnClickListener {
                    FirebaseAuth.getInstance().signOut()
                    requireActivity().finish()
                    IntentUtils().goToSignInActivity(this@MyAccountFragment.requireContext())
                }
            }

            // GET user details
            getUserDetails()
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

        // Set user details.
        binding.apply {
            textViewFullName.text = mUserDetails.fullName
            textViewEmailValue.text = mUserDetails.email
            textViewDateRegisteredValue.text = Constants.standardSimpleDateFormat.format(mUserDetails.dateRegistered)

            // If some details aren't set by the user we completely remove the view instead of
            // showing a blank view.
            if (mUserDetails.phoneNumber != "")
                if (mUserDetails.phoneCode.toString() != "")
                    textViewPhoneValue.text = String.format(getString(R.string.txt_format_phone), mUserDetails.phoneCode, mUserDetails.phoneNumber)
                else
                    textViewPhoneValue.text = mUserDetails.phoneNumber
            else textViewPhoneValue.text = getString(R.string.txt_none)

            if (mUserDetails.profImgUrl.isNotEmpty()) {
                binding.progressBarProfImgLoading.visibility = View.VISIBLE
                GlideLoader(this@MyAccountFragment.requireContext()).loadUserPicture(
                    mUserDetails.profImgUrl,
                    binding.imgViewUserPicture
                )
                binding.progressBarProfImgLoading.visibility = View.GONE
            }
        }

        unveilDetails()
    }

    /**
     * A function to UNVEIL all the veiled layouts in the fragment.
     */
    fun unveilDetails() {
        binding.apply {
            vLayoutHead.unVeil()
            vLayoutBody.unVeil()
        }
    }

    /**
     * A function to VEIL all the veiled layouts in the fragment.
     */
    private fun veilDetails() {
        binding.apply {
            vLayoutHead.veil()
            vLayoutBody.veil()
        }
    }

    /**
     * The fragment's onResume() will be called only when the Activity's onResume() is called.
     */
    override fun onResume() {
        super.onResume()

        init()
    }

    /**
     * We clean up any references to the binding class instance in the fragment's onDestroyView() method.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
