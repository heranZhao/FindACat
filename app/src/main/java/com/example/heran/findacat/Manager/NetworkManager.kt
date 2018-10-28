package com.example.heran.findacat.Manager

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkManager
{
    private val REQUEST_TYPE = 10
    private var permission = arrayOf(android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    fun requestPermission(act: Activity)
    {
        act.requestPermissions(permission, REQUEST_TYPE)
    }

    fun isNetworkAvailable(act: Activity): Boolean {
        val connectivityManager = act.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
}