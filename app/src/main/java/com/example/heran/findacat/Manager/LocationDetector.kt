package com.example.heran.findacat.Manager

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import java.util.*
import kotlin.concurrent.timerTask

class LocationDetector(private val context: Context) {
    val fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_TYPE = 10

    private var permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)

    init {
        fusedLocationClient = FusedLocationProviderClient(context)
    }

    //enum to describe reasons location detection might fail
    enum class FailureReason {
        TIMEOUT,
        NO_PERMISSION
    }

    var locationListener: LocationListener? = null

    interface LocationListener {
        fun locationFound(location: String?)
        fun locationNotFound(reason: FailureReason)
    }

    fun requestPermission(act: Activity)
    {
        act.requestPermissions(permission, REQUEST_TYPE)
    }

    fun detectLocation() {
        //create location request
        val locationRequest = LocationRequest()

        //check for location permission
        val permissionResult = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        //if location permission granted, proceed with location detection
        if(permissionResult == PackageManager.PERMISSION_GRANTED) {
            //create timer
            val timer = Timer()

            //create location detection callback
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    //stop location updates
                    fusedLocationClient.removeLocationUpdates(this)

                    //cancel timer
                    timer.cancel()

                    //fire callback with location
                    val zipcode = getPostalCodeByCoordinates(locationResult.locations.first().latitude, locationResult.locations.first().longitude)
                    locationListener?.locationFound(zipcode)
                }
            }

            //start a timer to ensure location detection ends after 10 seconds
            timer.schedule(timerTask {
                //if timer expires, stop location updates and fire callback
                fusedLocationClient.removeLocationUpdates(locationCallback)
                //run on UI thread
                (context as AppCompatActivity).runOnUiThread { locationListener?.locationNotFound(FailureReason.TIMEOUT) }
            }, 5*1000) //5 seconds


            //start location updates
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, null)
        }
        else {
            //else if no permission, fire callback
            locationListener?.locationNotFound(FailureReason.NO_PERMISSION)
        }
    }

    private fun getPostalCodeByCoordinates(Latitude : Double, Longitude : Double): String? {

        val mGeocoder = Geocoder(context, Locale.getDefault())
        var zipcode: String? = null
        var address: Address?

        val addresses = mGeocoder.getFromLocation(Latitude, Longitude, 5)

        if (addresses != null && addresses.size > 0) {

            for (i in addresses.indices) {
                address = addresses[i]
                if (address!!.getPostalCode() != null) {
                    zipcode = address.getPostalCode()
                    break
                }
            }
        }
        return zipcode
    }
}