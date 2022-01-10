package com.androidbolts.library.utils

class KalmanLatLong(Q_metres_per_second: Float) {

    private val MinAccuracy = 1f
    private var Q_metres_per_second: Float = 0.toFloat()
    private var TimeStamp_milliseconds: Long = 0
    private var lat: Double = 0.toDouble()
    private var lng: Double = 0.toDouble()
    private var variance: Float = -1f // P matrix. Negative means object uninitialised.
    var consecutiveRejectCount: Int = 0

    init {
        this.Q_metres_per_second = Q_metres_per_second
    }

    fun get_lat(): Double {
        return lat
    }

    fun get_lng(): Double {
        return lng
    }

    fun get_accuracy(): Float {
        return Math.sqrt(variance.toDouble()).toFloat()
    }

    fun Process(
        lat_measurement: Double, lng_measurement: Double,
        accuracy: Float, TimeStamp_milliseconds: Long, Q_metres_per_second: Float
    ) {
        var accuracy = accuracy
        this.Q_metres_per_second = Q_metres_per_second

        if (accuracy < MinAccuracy)
            accuracy = MinAccuracy
        if (variance < 0) {
            // if variance < 0, object is unitialised, so initialise with
            // current values
            this.TimeStamp_milliseconds = TimeStamp_milliseconds
            lat = lat_measurement
            lng = lng_measurement
            variance = accuracy * accuracy
        } else {
            // else apply Kalman filter methodology

            val TimeInc_milliseconds = TimeStamp_milliseconds - this.TimeStamp_milliseconds
            if (TimeInc_milliseconds > 0) {
                // time has moved on, so the uncertainty in the current position
                // increases
                variance += (TimeInc_milliseconds.toFloat() * Q_metres_per_second
                        * Q_metres_per_second) / 1000
                this.TimeStamp_milliseconds = TimeStamp_milliseconds
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