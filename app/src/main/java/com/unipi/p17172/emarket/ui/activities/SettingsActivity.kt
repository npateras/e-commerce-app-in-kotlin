package com.unipi.p17172.emarket.ui.activities

import android.app.ActionBar.DISPLAY_SHOW_CUSTOM
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapters.ViewPagerOrdersAdapter
import com.unipi.p17172.emarket.databinding.ActivityOrdersBinding
import com.unipi.p17172.emarket.ui.fragments.PendingOrdersFragment
import com.unipi.p17172.emarket.ui.fragments.PreviousOrdersFragment
import java.util.*


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
        init()
    }

    private fun setUpUI() {
        setUpActionBar()
        setupTabs()
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


    private fun setupTabs() {
        val adapter = ViewPagerOrdersAdapter(supportFragmentManager)
        adapter.addFragment(PendingOrdersFragment())
        adapter.addFragment(PreviousOrdersFragment())
        binding.viewPagerBody.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPagerBody)
    }

    private fun init() {

    }
}