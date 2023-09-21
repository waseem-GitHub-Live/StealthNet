package com.xilli.stealthnet.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object Utility {
    private const val TAG = "Utility"

    fun isOnline(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo: NetworkInfo? = cm.activeNetworkInfo
            return nInfo != null && nInfo.isConnected
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}