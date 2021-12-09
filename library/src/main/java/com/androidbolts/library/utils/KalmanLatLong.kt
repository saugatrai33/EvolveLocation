package com.androidbolts.library.utils

import kotlin.math.sqrt

class KalmanLatLong(Q_metres_per_second: Float) {

    private val minAccuracy = 1f

    private var qMetersPerSecond: Float = 0.toFloat()
    private var timeStampMilliSecond: Long = 0
    private var lat: Double = 0.toDouble()
    private var lng: Double = 0.toDouble()
    private var variance: Float = -1f // P matrix. Negative means object uninitialised.

    // NB: units irrelevant, as long as same units used
    // throughout
    var consecutiveRejectCount: Int = 0

    init {
        this.qMetersPerSecond = Q_metres_per_second
    }

    /*
    fun KalmanLatLong(Q_metres_per_second: Float): ??? {
        //this.Q_metres_per_second = Q_metres_per_second
        variance = -1f
        consecutiveRejectCount = 0
    }
    */

    fun get_TimeStamp(): Long {
        return timeStampMilliSecond
    }

    fun lat(): Double {
        return lat
    }

    fun lng(): Double {
        return lng
    }

    fun acc(): Float {
        return sqrt(variance.toDouble()).toFloat()
    }

    fun SetState(
        lat: Double, lng: Double, accuracy: Float,
        TimeStamp_milliseconds: Long
    ) {
        this.lat = lat
        this.lng = lng
        variance = accuracy * accuracy
        this.timeStampMilliSecond = TimeStamp_milliseconds
    }

    // / <summary>
    // / Kalman filter processing for lattitude and longitude
    // / </summary>
    // / <param name="lat_measurement_degrees">new measurement of
    // lattidude</param>
    // / <param name="lng_measurement">new measurement of longitude</param>
    // / <param name="accuracy">measurement of 1 standard deviation error in
    // metres</param>
    // / <param name="TimeStamp_milliseconds">time of measurement</param>
    // / <returns>new state</returns>
    fun process(
        lat_measurement: Double, lng_measurement: Double,
        accuracy: Float, TimeStamp_milliseconds: Long, Q_metres_per_second: Float
    ) {
        var accuracy = accuracy
        this.qMetersPerSecond = Q_metres_per_second

        if (accuracy < minAccuracy)
            accuracy = minAccuracy
        if (variance < 0) {
            // if variance < 0, object is unitialised, so initialise with
            // current values
            this.timeStampMilliSecond = TimeStamp_milliseconds
            lat = lat_measurement
            lng = lng_measurement
            variance = accuracy * accuracy
        } else {
            // else apply Kalman filter methodology

            val TimeInc_milliseconds = TimeStamp_milliseconds - this.timeStampMilliSecond
            if (TimeInc_milliseconds > 0) {
                // time has moved on, so the uncertainty in the current position
                // increases
                variance += (TimeInc_milliseconds.toFloat() * Q_metres_per_second
                        * Q_metres_per_second) / 1000
                this.timeStampMilliSecond = TimeStamp_milliseconds
                // TO DO: USE VELOCITY INFORMATION HERE TO GET A BETTER ESTIMATE
                // OF CURRENT POSITION
            }

            // Kalman gain matrix K = Covarariance * Inverse(Covariance +
            // MeasurementVariance)
            // NB: because K is dimensionless, it doesn't matter that variance
            // has different units to lat and lng
            val K = variance / (variance + accuracy * accuracy)
            // apply K
            lat += K * (lat_measurement - lat)
            lng += K * (lng_measurement - lng)
            // new Covarariance matrix is (IdentityMatrix - K) * Covarariance
            variance = (1 - K) * variance
        }
    }
}