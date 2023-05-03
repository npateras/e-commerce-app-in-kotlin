package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.databinding.ActivityForgotPasswordBinding
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.snackBarSuccessLargeClass
import com.unipi.mpsp21043.client.utils.textInputLayoutError
import com.unipi.mpsp21043.client.utils.textInputLayoutNormal

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        setupUI()
        setUpActionBar()
        setUpClickListeners()
    }

    private fun setupUI() {
        binding.apply {
            textInputEditTextEmail.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutNormal(textInputLayoutEmail)
                }
            })
        }
    }

    private fun setUpClickListeners() {
        binding.apply {
            buttonSend.setOnClickListener{ resetPassword() }
        }
    }


    private fun resetPassword() {
        binding.apply {
            if (validateFields()) {
                // Show the progress dialog.
                showProgressDialog()

                // This piece of code is used to send the reset password link to the user's email id if the user is registered.
                FirebaseAuth.getInstance().sendPasswordResetEmail(textInputEditTextEmail.text.toString())
                    .addOnCompleteListener { task ->

                        // Hide the progress dialog
                        hideProgressDialog()

                        if (task.isSuccessful) {
                            snackBarSuccessLargeClass(root, getString(R.string.text_password_reset_mail_sent))
                            finish()
                        } else
                            snackBarErrorClass(root, task.exception!!.message.toString())
                    }
            }
            else
                buttonSend.startAnimation(AnimationUtils.loadAnimation(this@ForgotPasswordActivity, R.anim.shake))
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextEmail.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_email))
                    textInputLayoutError(textInputLayoutEmail, getString(R.string.text_error_empty_email))
                    false
                }

                else -> true
            }
        }
    }

    private fun setUpActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            textViewActionLabel.text = getString(R.string.text_forgot_password)
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }
}
