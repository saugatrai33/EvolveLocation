package com.androidbolts.saugatlocationmanager.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.androidbolts.library.LocationManager
import com.androidbolts.saugatlocationmanager.R
import com.androidbolts.saugatlocationmanager.base.BaseActivity
import com.androidbolts.saugatlocationmanager.databinding.ActivityLocationBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment

class LocationActivity : BaseActivity() {

    private lateinit var binding: ActivityLocationBinding
    private var location: Location? = null
    private var locationManager: LocationManager? = null

    private var map: GoogleMap? = null
    companion object {
        fun getIntent(activity: Activity): Intent {
            return Intent(activity, LocationActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Location Activity"
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        getLocation()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            this.map = googleMap

//            map.uiSettings.isZoomControlsEnabled = false
//            map.isMyLocationEnabled = false

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@getMapAsync
            }
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true

            map!!.uiSettings.isCompassEnabled = true
            map!!.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun getLocation() {
        locationManager = initLocationManager()
        lifecycle.addObserver(locationManager!!)
        locationManager?.getLocation()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onLocationChanged(location: Location?) {
        this.location = location
        Log.d(
            "Location fetched:",
            "${this.location?.latitude}, ${this.location?.longitude}, accuracy:: ${location!!.accuracy}"
        )
        binding.tvCurrentLocation.text = """Latitude: ${this.location?.latitude}
                                            Longitude: ${this.location?.longitude}
                                            Accuracy: ${this.location?.accuracy}"""

    }

}