package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivitySignUpBinding
import com.unipi.mpsp21043.client.models.User
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.textInputLayoutError


class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        setupUI()
        setupActionBar()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.apply {
            textInputEditTextFullName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutFullName.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            textInputEditTextEmail.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutEmail.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            textInputEditTextPassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutPassword.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            textViewSignIn.setOnClickListener { IntentUtils().goToSignInActivity (this@SignUpActivity)}
            buttonSignUp.setOnClickListener { registerUser() }
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateRegisterDetails(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextFullName.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_name))
                    textInputLayoutError(textInputLayoutFullName, getString(R.string.text_error_empty_name))
                    false
                }

                TextUtils.isEmpty(textInputEditTextEmail.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_email))
                    textInputLayoutError(textInputLayoutEmail, getString(R.string.text_error_empty_email))
                    false
                }

                TextUtils.isEmpty(textInputEditTextPassword.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_password))
                    textInputLayoutError(textInputLayoutPassword, getString(R.string.text_error_empty_password))
                    false
                }

                else -> true
            }
        }
    }

    /**
     * A function to register the user with email and password using FirebaseAuth.
     */
    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            // Show the progress dialog.
            showProgressDialog()

            binding.apply  {
                val email: String = textInputEditTextEmail.text.toString().trim { it <= ' ' }
                val password: String = textInputEditTextPassword.text.toString().trim { it <= ' ' }

                // Create an instance and create a register a user with email and password.
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            // Instance of User data model class.
                            val user = User(
                                firebaseUser.uid,
                                textInputEditTextFullName.text.toString().trim { it <= ' ' },
                                textInputEditTextEmail.text.toString().trim { it <= ' ' }
                            )


                            // Pass the required values in the constructor.
                            FirestoreHelper().registerUser(this@SignUpActivity, user)
                        } else {

                            // Hide the progress dialog
                            hideProgressDialog()

                            // If the registering is not successful then show the error message.
                            snackBarErrorClass(root, task.exception!!.message.toString())
                        }
                    }
            }
        }
        else
            binding.buttonSignUp.startAnimation(AnimationUtils.loadAnimation(this@SignUpActivity, R.anim.shake))
    }

    /**
     * A function to notify the success result of Firestore entry when the user is registered successfully.
     */
    fun userRegistrationSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()

        goToSignInActivity(this@SignUpActivity,
            true,
            binding.textInputEditTextEmail.text.toString())

        // Finish the Register Screen
        finish()
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(toolbar)
            textViewActionLabel.text = getString(R.string.text_sign_up)
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
