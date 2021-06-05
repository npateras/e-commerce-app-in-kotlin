package com.unipi.p17172.emarket.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            txtViewSignIn.setOnClickListener {
                // Launch the sign up screen when the user clicks on the sign up text.
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                startActivity(intent)
            }
            btnSignUp.setOnClickListener{ registerUser() }
        }
    }

    private fun registerUser() {

    }
}