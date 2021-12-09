package com.androidbolts.library.utils

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi

object KalmanUtils {

    private val LOG_TAG = KalmanUtils::class.java.simpleName

    private var currentSpeed = 0.0f // meters/second
    private var kalmanFilter = KalmanLatLong(3f)
    var runStartTimeInMillis: Long = 0

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun kalmanFilter(location: Location, kCallback: (loc: Location) -> Unit) {
        val age = getLocationAge(location)
        if (age > 5 * 1000) { //more than 5 seconds
            Log.d(LOG_TAG, "Location is old")
            kCallback(location)
            return
        }

        if (location.accuracy <= 0) {
            Log.d(LOG_TAG, "Latitidue and longitude values are invalid.")
            kCallback(location)
            return
        }

        val horizontalAccuracy = location.accuracy
        if (horizontalAccuracy > 1000) { // 10meter filter
            Log.d(LOG_TAG, "Accuracy is too low.")
            kCallback(location)
            return
        }

        /* Kalman Filter */
        var Qvalue: Float = 3.0f

        val locationTimeInMillis = location.elapsedRealtimeNanos / 1000000
        val elapsedTimeInMillis = locationTimeInMillis - runStartTimeInMillis

        Qvalue = if (currentSpeed == 0.0f) {
            3.0f //3 meters per second
        } else {
            currentSpeed // meters per second
        }

        kalmanFilter.process(
            location.latitude,
            location.longitude,
            location.accuracy,
            elapsedTimeInMillis,
            Qvalue
        )
        val predictedLat = kalmanFilter.lat()
        val predictedLng = kalmanFilter.lng()

        val predictedLocation = Location(LocationManager.GPS_PROVIDER)//provider name is unecessary
        predictedLocation.latitude = predictedLat//your coords of course
        predictedLocation.longitude = predictedLng
        predictedLocation.accuracy = kalmanFilter.acc()
        val predictedDeltaInMeters = predictedLocation.distanceTo(location)

        if (predictedDeltaInMeters > 60) {
            Log.d(
                LOG_TAG,
                "Kalman Filter detects mal GPS, we should probably remove this from track"
            )
            kalmanFilter.consecutiveRejectCount += 1

            if (kalmanFilter.consecutiveRejectCount > 3) {
                kalmanFilter =
                    KalmanLatLong(3f) //reset Kalman Filter if it rejects more than 3 times in raw.
            }

            kCallback(location)
            return
        } else {
            kalmanFilter.consecutiveRejectCount = 0
        }

        Log.d(
            LOG_TAG,
            "lat:: ${predictedLocation.latitude}, lng:: ${predictedLocation.longitude}, AccuracyWithKalmanFilter:: ${predictedLocation.accuracy}"
        )
        kCallback(predictedLocation)
    }

    @SuppressLint("NewApi")
    private fun getLocationAge(newLocation: Location): Long {
        val locationAge: Long = if (Build.VERSION.SDK_INT >= 17) {
            val currentTimeInMilli = SystemClock.elapsedRealtimeNanos() / 1000000
            val locationTimeInMilli = newLocation.elapsedRealtimeNanos / 1000000
            currentTimeInMilli - locationTimeInMilli
        } else {
            System.currentTimeMillis() - newLocation.time
        }
        return locationAge
    }

}