package com.example.heran.findacat.Manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat.startActivity
import java.io.IOException
import java.util.*


class MyLocationManager
{
    private lateinit var locationManager : LocationManager

    private var hasGps = false
    private var hasNetWork = false
    private var locationGps: Location? = null
    private var locationNetWork: Location? = null

    private var Longitude : Double = 0.0
    private var Latitude : Double = 0.0

    private lateinit var activity : Activity

    private var permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,android.Manifest.permission.INTERNET)

    public fun getLocation(act : Activity) : String?
    {

        activity = act

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            if(!checkPermission())
            {
                activity.requestPermissions(permission, 10)
            }
        }



        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetWork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(hasGps && hasNetWork)
        {
            if(hasGps)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0F, object: LocationListener {
                    override fun onLocationChanged(location: Location?)
                    {
                        if(location != null)
                        {
                            locationGps = location
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                })

                val localGpslocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(localGpslocation != null)
                {
                    locationGps = localGpslocation
                }
            }
            if(hasNetWork)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0F, object: LocationListener {
                    override fun onLocationChanged(location: Location?)
                    {
                        if(location != null)
                        {
                            locationNetWork = location
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                })

                val localNetWorklocation= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(localNetWorklocation != null)
                {
                    locationNetWork = localNetWorklocation
                }
            }

            Longitude = locationGps!!.longitude
            Latitude = locationGps!!.latitude

            return getPostalCodeByCoordinates()
//
//            if(locationGps != null && locationNetWork != null)
//            {
//                if(locationGps!!.accuracy > locationNetWork!!.accuracy)
//                {
//
//                }
//                else
//                {
//                    Longitude = locationNetWork!!.longitude
//                    Latitude = locationNetWork!!.latitude
//                }
//            }
        }
        else if(!hasGps)
        {
            startActivity(activity, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),null)

        }
        else if(!hasNetWork)
        {
            startActivity(activity, Intent(android.provider.Settings.ACTION_NETWORK_OPERATOR_SETTINGS), null)
        }
        return null
    }

    private fun checkPermission() : Boolean
    {
        var allSuccess = true
        for(i in permission.indices)
        {
            if(activity.checkCallingOrSelfPermission(permission[i]) == PackageManager.PERMISSION_DENIED)
            {
                allSuccess = false
            }
        }
        return allSuccess
    }

    private fun requestPermission()
    {

    }


    @Throws(IOException::class)
    private fun getPostalCodeByCoordinates(): String? {

        val mGeocoder = Geocoder(activity, Locale.getDefault())
        var zipcode: String? = null
        var address: Address? = null

        if (mGeocoder != null) {

            val addresses = mGeocoder.getFromLocation(Latitude, Longitude, 5)

            if (addresses != null && addresses.size > 0) {

                for (i in addresses.indices) {
                    address = addresses[i]
                    if (address!!.getPostalCode() != null) {
                        zipcode = address!!.getPostalCode()
                        break
                    }

                }
                return zipcode
            }
        }

        return null
    }
}