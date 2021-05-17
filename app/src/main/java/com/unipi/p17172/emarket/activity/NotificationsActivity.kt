package com.unipi.p17172.emarket.activity

import android.app.ActionBar.DISPLAY_SHOW_CUSTOM
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.databinding.ActivityNotificationsBinding
import java.util.*


class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
        init()
    }

    private fun setUpUI() {
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)
        Objects.requireNonNull(supportActionBar)!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.let {
            it.displayOptions = DISPLAY_SHOW_CUSTOM
            it.setCustomView(R.layout.toolbar_activity_main)
        }
    }

    private fun init() {

    }

    private fun loadNotifications() {

    }
}