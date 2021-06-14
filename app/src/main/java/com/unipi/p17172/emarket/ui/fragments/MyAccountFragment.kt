package com.unipi.p17172.emarket.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.FragmentMyAccountBinding
import com.unipi.p17172.emarket.models.User
import com.unipi.p17172.emarket.ui.activities.MainActivity

class MyAccountFragment : BaseFragment() {
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentMyAccountBinding? = null
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
                btnUpdateProfile.setOnClickListener { goToUpdateProfileActivity(this@MyAccountFragment.requireContext()) }
                btnLogOut.setOnClickListener{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, MainActivity::class.java)
                    requireActivity().finishAffinity()
                    startActivity(intent)
                }
            }

            // GET user details
            getUserDetails()
        }
        else // If user is not signed in
            binding.apply {
                // We make the sign in layout visible and add the button click listeners accordingly.
                layoutMustBeSignedIn.apply {
                    root.visibility = View.VISIBLE
                    btnSignIn.setOnClickListener{ goToSignInActivity(this@MyAccountFragment.requireContext()) }
                    txtViewSignUp.setOnClickListener{ goToSignInActivity(this@MyAccountFragment.requireContext()) }
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
        // Set user details.
        binding.apply {
            textViewFullName.text = mUser.fullName
            textViewEmailValue.text = mUser.email
            // If some details aren't set by the user we completely remove the view instead of
            // showing a blank view.
            if (mUser.address == "") {
                textViewAddress.visibility = View.GONE
                textViewAddressValue.visibility = View.GONE
            }
            else textViewAddressValue.text = mUser.address

            if (mUser.phone == "") {
                textViewPhone.visibility = View.GONE
                textViewPhoneValue.visibility = View.GONE
            }
            else textViewPhoneValue.text = String.format(
                getString(R.string.txt_format_phone),
                mUser.phoneCode, mUser.phone)
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
     * We clean up any references to the binding class instance in the fragment's onDestroyView() method.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}