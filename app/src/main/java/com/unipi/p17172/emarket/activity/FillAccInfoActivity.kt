package com.unipi.p17172.emarket.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.databinding.ActivityAccFillInfoBinding
import com.unipi.p17172.emarket.util.CustomDialog

class FillAccInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccFillInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccFillInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        CustomDialog.showDialogNumberVerified(this)
    }

    override fun onBackPressed() {
        // Empty so we can stop the back button press
    }
}