package com.unipi.p17172.emarket.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.databinding.ActivitySignInPhoneBinding

class SignInPhoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInPhoneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        // actionbar
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        /*binding.btnSignIn.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}