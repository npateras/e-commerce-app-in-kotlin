package com.unipi.p17172.emarket.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        binding.btnSignInWithPhone.setOnClickListener{
            val intent = Intent(this, SignInPhoneActivity::class.java)
            startActivity(intent)
        }
    }
}