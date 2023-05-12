package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.databinding.ActivityAuthenticateBinding
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.textInputLayoutError
import com.unipi.mpsp21043.client.utils.textInputLayoutNormal


class AuthenticateActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthenticateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_AUTHENTICATE_CHANGE_PASSWORD)) {
            if (intent.extras?.getBoolean(Constants.EXTRA_AUTHENTICATE_CHANGE_PASSWORD) == true) {
                binding.apply {
                    textViewHeader1.setText(R.string.text_authenticate)
                    textViewHeader2.setText(R.string.text_authenticate_2)
                    buttonAuthenticate.setText(R.string.text_authenticate)
                }
            }
        }

        setupUI()
        setupActionBar()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.apply {
            textInputEditTextPassword.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutNormal(textInputLayoutPassword)
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            textViewForgotPassword.setOnClickListener { IntentUtils().goToForgotPasswordActivity(this@AuthenticateActivity) }
            buttonAuthenticate.setOnClickListener { authenticateUser() }
        }
    }

    private fun authenticateUser() {
        if (validateFields()) {

            binding.apply {
                // Get the text from editText and trim the space
                val password = textInputEditTextPassword.text.toString().trim { it <= ' ' }

                FirebaseAuth.getInstance().currentUser?.let { user ->
                    val credential = EmailAuthProvider.getCredential(user.email!!, password)
                    showProgressDialog()
                    user.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            hideProgressDialog()
                            when {
                                task.isSuccessful -> {
                                    finish()
                                    IntentUtils().goToChangePasswordActivity(this@AuthenticateActivity)
                                }
                                task.exception is FirebaseAuthInvalidCredentialsException -> {
                                    snackBarErrorClass(root, getString(R.string.text_error_invalid_password))
                                    textInputLayoutError(textInputLayoutPassword)
                                }
                                else -> {
                                    snackBarErrorClass(root, task.exception?.message!!)
                                }
                            }
                        }
                }
            }
        }
        else
            binding.buttonAuthenticate.startAnimation(AnimationUtils.loadAnimation(this@AuthenticateActivity, R.anim.shake))
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextPassword.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_password))
                    textInputLayoutError(textInputLayoutPassword)
                    false
                }

                else -> true
            }
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(toolbar)
            textViewActionLabel.text = ""
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
