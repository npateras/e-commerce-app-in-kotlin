package com.unipi.mpsp21043.admin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.database.FirestoreHelper
import com.unipi.mpsp21043.admin.databinding.ActivitySignInBinding
import com.unipi.mpsp21043.admin.models.User
import com.unipi.mpsp21043.admin.utils.Constants
import com.unipi.mpsp21043.admin.utils.SnackBarErrorClass


class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Enable support for Splash Screen API for
        // proper Android 12+ support
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                isLoading
            }
        }

        if (FirestoreHelper().getCurrentUserID() != "")
            goToMainActivity(this@SignInActivity)
        else
            isLoading = false

        init()
    }

    private fun init() {
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
                    textInputLayoutEmail.apply {
                        isErrorEnabled = false
                        background = AppCompatResources.getDrawable(this@SignInActivity, R.drawable.text_input_background)
                    }
                }
            })

            textInputEditTextPassword.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutPassword.apply {
                        isErrorEnabled = false
                        background = AppCompatResources.getDrawable(this@SignInActivity, R.drawable.text_input_background)
                    }
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            textViewForgotPassword.setOnClickListener {
                // Launch the forgot password screen when the user clicks on the forgot password text.
                val intent = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
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
                        } else {
                            // Hide the progress dialog
                            hideProgressDialog()
                            SnackBarErrorClass
                                .make(root, task.exception!!.message.toString())
                                .show()
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

        if (user.role != Constants.ROLE_ADMIN) {
            SnackBarErrorClass
                .make(binding.root, getString(R.string.text_error_user_not_admin))
                .show()

        }
        else {
            // Redirect the admin to the dashboard Screen after logging in.
            goToMainActivity(this@SignInActivity)
            finish()
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextEmail.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.text_error_empty_email))
                        .show()
                    textInputLayoutEmail.apply {
                        requestFocus()
                        error = getString(R.string.text_error_empty_email)
                        background = AppCompatResources.getDrawable(this@SignInActivity, R.drawable.text_input_background_error)
                    }
                    false
                }

                TextUtils.isEmpty(textInputEditTextPassword.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.text_error_empty_password))
                        .show()
                    textInputLayoutPassword.apply {
                        requestFocus()
                        error = getString(R.string.text_error_empty_password)
                        background = AppCompatResources.getDrawable(this@SignInActivity, R.drawable.text_input_background_error)
                    }
                    false
                }

                else -> true
            }
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            textViewActionLabel.text = getString(R.string.text_sign_in)
        }
    }

}
