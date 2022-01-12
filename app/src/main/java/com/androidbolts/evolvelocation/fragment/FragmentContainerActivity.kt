package com.androidbolts.evolvelocation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.androidbolts.evolvelocation.R
import com.androidbolts.evolvelocation.databinding.ActivityFragmentContainerBinding

class FragmentContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentContainerBinding

    companion object {
        fun getIntent(activity: Activity): Intent {
            return Intent(activity, FragmentContainerActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Location Fragment"
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_fragment_container
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                LocationFragment.getInstance()
            ).commitNow()
    }
}