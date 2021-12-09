package com.androidbolts.saugatlocationmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.androidbolts.saugatlocationmanager.activity.LocationActivity
import com.androidbolts.saugatlocationmanager.databinding.ActivityMainBinding
import com.androidbolts.saugatlocationmanager.fragment.FragmentContainerActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.tvActivity.setOnClickListener {
            startActivity(LocationActivity.getIntent(this))
        }

        binding.tvFragment.setOnClickListener {
            startActivity(FragmentContainerActivity.getIntent(this))
        }
    }
}
