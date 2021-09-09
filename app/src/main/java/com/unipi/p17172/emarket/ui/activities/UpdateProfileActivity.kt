package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.databinding.ActivityUserProfileEditBinding
import com.unipi.p17172.emarket.models.User
import com.unipi.p17172.emarket.utils.Constants


class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileEditBinding
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setupUI()

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!

            binding.apply {
                inputTxtFirstName.setText(mUserDetails.fullName)
            }
        }
    }

    private fun setupUI() {
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.imgBtnSave.setOnClickListener {

            }
        }
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }
}
