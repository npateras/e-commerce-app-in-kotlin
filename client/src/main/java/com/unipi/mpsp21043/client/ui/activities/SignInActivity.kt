package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.window.OnBackInvokedDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivitySignInBinding
import com.unipi.mpsp21043.client.models.User
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.snackBarSuccessLargeClass
import com.unipi.mpsp21043.client.utils.textInputLayoutError
import com.unipi.mpsp21043.client.utils.textInputLayoutNormal


class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_REG_USERS_SNACKBAR)) {
            binding.apply {
                if (intent.extras?.getBoolean(Constants.EXTRA_REG_USERS_SNACKBAR) == true)
                    snackBarSuccessLargeClass(root, getString(R.string.text_account_created))
                // If we just returned from sign up activity, we set the email input to the one that has just been created.
                textInputEditTextEmail.setText(intent.getStringExtra(Constants.EXTRA_USER_EMAIL))
            }
        }

        setupUI()
        setupActionBar()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.apply {
            textInputEditTextEmail.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutNormal(textInputLayoutEmail)
                }
            })

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
            textViewSignUp.setOnClickListener { IntentUtils().goToSignUpActivity(this@SignInActivity) }
            textViewForgotPassword.setOnClickListener { IntentUtils().goToForgotPasswordActivity(this@SignInActivity) }
            buttonSignIn.setOnClickListener { signInUser() }
        }
    }

    private fun signInUser() {
        if (validateFields()) {
            // Show the progress dialog.
            showProgressDialog()

            binding.apply {
                // Get the text from editText and trim the space
                val email = textInputEditTextEmail.text.toString().trim { it <= ' ' }
                val password = textInputEditTextPassword.text.toString().trim { it <= ' ' }

                // Log-In using FirebaseAuth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            FirestoreHelper().getUserDetails(this@SignInActivity)
                        }
                        else {
                            // Hide the progress dialog
                            hideProgressDialog()
                            snackBarErrorClass(root, task.exception!!.message.toString())
                        }
                    }
            }
        }
        else
            binding.buttonSignIn.startAnimation(AnimationUtils.loadAnimation(this@SignInActivity, R.anim.shake))
    }

    /**
     * A function to notify user that logged in success and get the user details from the FireStore database after authentication.
     */
    fun userLoggedInSuccess(user: User) {

        // Hide the progress dialog.
        hideProgressDialog()

        IntentUtils().createNewMainActivity(this@SignInActivity)
        /*if (!user.profileCompleted) {
            goToMainActivity(this@SignInActivity, true)
            finish()
        }
        else {
            // Redirect the user to Dashboard Screen after log in.
            goToMainActivity(this@SignInActivity)
            finish()
        }*/
        finish()
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextEmail.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_email))
                    textInputLayoutError(textInputLayoutEmail)
                    false
                }

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
            textViewActionLabel.text = getString(R.string.text_login)
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        goToMainActivity(this@SignInActivity)
        return super.getOnBackInvokedDispatcher()
    }

}
