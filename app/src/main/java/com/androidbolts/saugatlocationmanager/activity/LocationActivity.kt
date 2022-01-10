package com.androidbolts.saugatlocationmanager.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import com.androidbolts.library.LocationManager
import com.androidbolts.saugatlocationmanager.R
import com.androidbolts.saugatlocationmanager.base.BaseActivity
import com.androidbolts.saugatlocationmanager.databinding.ActivityLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

val TAG = LocationActivity::class.java.canonicalName

class LocationActivity : BaseActivity() {

    private lateinit var binding: ActivityLocationBinding
    private var locationManager: LocationManager? = null
    private var map: GoogleMap? = null
    private var locationAccuracyCircle: Circle? = null
    private var latLng: LatLng? = null
    private var accuracy: Double? = null
    private var zoom: Float = 19.5f
    private var predictionRange: Circle? = null
    private var icLocationCurrent: BitmapDescriptor? = null
    private var bitmap: Bitmap? = null

    companion object {
        fun getIntent(activity: Activity): Intent {
            return Intent(activity, LocationActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Location Activity"
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_location
        )
        getLocation()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            this.map = googleMap
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@getMapAsync
            }

//            map?.isMyLocationEnabled = true
            map?.uiSettings?.isZoomControlsEnabled = true
//            map?.uiSettings?.isMyLocationButtonEnabled = true
            map?.uiSettings?.isCompassEnabled = true
//            map?.isBuildingsEnabled = true
            icLocationCurrent = BitmapDescriptorFactory.fromResource(R.drawable.ic_shutter_normal)
            bitmap = R.drawable.ic_shutter_normal.toBitmap(this, R.color.colorAccent)
        }

        binding.btnCurrentLocation.setOnClickListener {
            cameraAnim2()
        }
    }

    private fun getLocation() {
        locationManager = initLocationManager()
        lifecycle.addObserver(locationManager!!)
        locationManager?.getLocation()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onLocationChanged(location: Location?) {
        if (location?.accuracy!! < 0) return
        latLng = LatLng(location.latitude, location.longitude)
        accuracy = location.accuracy.toDouble()
        zoom = map?.cameraPosition?.zoom!!
//        showLocationAccuracyCircle()
//        drawPredictedRange()
        drawUserLocation()
        cameraAnim()
        binding.tvCurrentLocation.text = """Lat: ${location?.latitude}
                                            Lng: ${location?.longitude}
                                            Acc: ${location?.accuracy}
                                          """
    }

    private fun drawPredictedRange() {
        predictionRange?.let {
            it.center = latLng!!
        } ?: run {
            predictionRange = map?.addCircle(
                CircleOptions()
                    .center(latLng!!)
                    .fillColor(Color.argb(50, 30, 207, 0))
                    .strokeColor(Color.argb(128, 30, 207, 0))
                    .strokeWidth(1.0f)
                    .radius(30.0)
            ) //30 meters of the prediction range
        }
    }

    private fun showLocationAccuracyCircle() {
        locationAccuracyCircle?.let {
            it.center = latLng!!
        } ?: run {
            this.locationAccuracyCircle = map?.addCircle(
                CircleOptions()
                    .center(latLng!!)
                    .fillColor(Color.argb(64, 0, 0, 0))
                    .strokeColor(Color.argb(64, 0, 0, 0))
                    .strokeWidth(0.0f)
                    .radius(accuracy!!)
            ) //set radius to horizonal accuracy in meter.
        }
    }

    private fun cameraAnim(zoomLevel: Float = 20f) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng!!) // Sets the center of the map to Mountain View
            .zoom(zoom)            // Sets the zoom
            .bearing(90f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()              // Creates a CameraPosition from the builder
        map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun cameraAnim2() {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng!!) // Sets the center of the map to Mountain View
            .zoom(20f)            // Sets the zoom
            .bearing(90f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()              // Creates a CameraPosition from the builder
        map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun drawUserLocation() {
        map?.clear()
        map?.addMarker(
            MarkerOptions()
                .title("Current Location")
                .snippet("Current Loc")
                .position(latLng!!)
                .flat(true)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap!!))
        )
    }

    private fun Int.toBitmap(context: Context, @ColorRes tintColor: Int? = null): Bitmap? {

        // retrieve the actual drawable
        val drawable = ContextCompat.getDrawable(context, this) ?: return null
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bm = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // add the tint if it exists
        tintColor?.let {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, it))
        }
        // draw it onto the bitmap
        val canvas = Canvas(bm)
        drawable.draw(canvas)
        return bm
    }
}