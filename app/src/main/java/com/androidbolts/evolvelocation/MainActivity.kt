package com.androidbolts.evolvelocation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.androidbolts.evolvelocation.activity.LocationActivity
import com.androidbolts.evolvelocation.databinding.ActivityMainBinding
import com.androidbolts.evolvelocation.fragment.FragmentContainerActivity

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
