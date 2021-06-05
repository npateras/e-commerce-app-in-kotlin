package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    init {
        setUpActionBar()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.apply {
            btnSend.setOnClickListener{ resetPassword() }
        }
    }


    private fun resetPassword() {

    }

    private fun setUpActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            textViewActionBarLabel.text = getString(R.string.txt_forgot_password)
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }

}