package com.unipi.mpsp21043.client.ui.activities.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.window.OnBackInvokedDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.databinding.ActivityChangePasswordBinding
import com.unipi.mpsp21043.client.ui.activities.BaseActivity
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.snackBarSuccessLargeClass
import com.unipi.mpsp21043.client.utils.textInputLayoutError
import com.unipi.mpsp21043.client.utils.textInputLayoutNormal


class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
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
            textInputEditTextNewPassword.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutNormal(textInputLayoutNewPassword)
                }
            })
            textInputEditTextConfirmNewPassword.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutNormal(textInputLayoutConfirmNewPassword)
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonSave.setOnClickListener { saveNewPassword() }
        }
    }

    private fun saveNewPassword() {
        if (validateFields()) {

            binding.apply {
                // Get the text from editText and trim the space
                val newPassword = textInputEditTextNewPassword.text.toString().trim { it <= ' ' }

                FirebaseAuth.getInstance().currentUser?.let { user ->
                    showProgressDialog()
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            hideProgressDialog()
                            if (task.isSuccessful) {
                                // Show snackbar & disable button to prevent duplicate tasks
                                binding.buttonSave.isEnabled = false
                                snackBarSuccessLargeClass(root, getString(R.string.text_password_updated))

                                // Go back to settings after 2 seconds
                                Handler(Looper.getMainLooper()).postDelayed({
                                    finish()
                                    IntentUtils().goToListSettingsActivity(this@ChangePasswordActivity)
                                }, 2000)
                            }
                            else
                                snackBarErrorClass(root, task.exception?.message!!)
                        }
                }
            }
        }
        else
            binding.buttonSave.startAnimation(AnimationUtils.loadAnimation(this@ChangePasswordActivity, R.anim.shake))
    }

    private fun validateFields(): Boolean {
        binding.apply {
            val newPassword = textInputEditTextNewPassword.text.toString().trim { it <= ' ' }
            val confirmNewPassword = textInputEditTextConfirmNewPassword.text.toString().trim { it <= ' ' }

            return when {
                TextUtils.isEmpty(newPassword) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_new_password))
                    textInputLayoutError(textInputLayoutNewPassword, getString(R.string.text_error_empty_new_password))
                    false
                }

                TextUtils.isEmpty(confirmNewPassword) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_confirm_new_password))
                    textInputLayoutError(textInputLayoutConfirmNewPassword, getString(R.string.text_error_empty_confirm_new_password))
                    false
                }

                (newPassword != confirmNewPassword) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_new_password_does_not_match))
                    textInputLayoutError(textInputLayoutConfirmNewPassword, getString(R.string.text_error_new_password_does_not_match))
                    false
                }

                else -> true
            }
        }
    }

    private fun setupActionBar() {
        binding.actionBarWithToolbar.apply {
            setSupportActionBar(toolbar)
            imageButtonSave.setOnClickListener { saveNewPassword() }
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        finish()
        return super.getOnBackInvokedDispatcher()
    }
}
