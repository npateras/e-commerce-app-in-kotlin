package com.unipi.p17172.emarket.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.databinding.ActivitySignInPhoneVerificationBinding

class SignInPhoneVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInPhoneVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInPhoneVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        // actionbar
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        binding.btnSignInPhoneVerificationVerify.setOnClickListener{
            val intent = Intent(this, FillAccInfoActivity::class.java)
            startActivity(intent)
        }
        binding.inputTxtCode1.setOnKeyListener( { v, keyCode, event ->
            if (!binding.inputTxtCode1.text.isNullOrEmpty() &&
                (keyCode != KeyEvent.KEYCODE_DEL &&
                        keyCode != KeyEvent.KEYCODE_DPAD_LEFT &&
                        keyCode != KeyEvent.KEYCODE_DPAD_RIGHT &&
                        keyCode != KeyEvent.KEYCODE_BACK)) {
                binding.inputTxtCode2.requestFocus()
                return@setOnKeyListener true
            }
            false
        })
        binding.inputTxtCode2.setOnKeyListener( { v, keyCode, event ->
            if (!binding.inputTxtCode2.text.isNullOrEmpty() &&
                    (keyCode != KeyEvent.KEYCODE_DEL &&
                            keyCode != KeyEvent.KEYCODE_DPAD_LEFT &&
                            keyCode != KeyEvent.KEYCODE_DPAD_RIGHT &&
                            keyCode != KeyEvent.KEYCODE_BACK)) {
                binding.inputTxtCode3.requestFocus()
                return@setOnKeyListener true
            }
            false
        })
        binding.inputTxtCode3.setOnKeyListener( { v, keyCode, event ->
            if (!binding.inputTxtCode3.text.isNullOrEmpty() &&
                (keyCode != KeyEvent.KEYCODE_DEL &&
                        keyCode != KeyEvent.KEYCODE_DPAD_LEFT &&
                        keyCode != KeyEvent.KEYCODE_DPAD_RIGHT &&
                        keyCode != KeyEvent.KEYCODE_BACK)) {
                binding.inputTxtCode4.requestFocus()
                return@setOnKeyListener true
            }
            false
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}