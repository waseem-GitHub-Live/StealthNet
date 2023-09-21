package com.xilli.stealthnet.helper

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Settings
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.xilli.stealthnet.R
import com.xilli.stealthnet.ui.services.VpnServices
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object AppHelper {
    var vpnService: VpnServices? = null

    fun getGeolocationData(callback: GeolocationCallback) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://ipinfo.io/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                callback.onError(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                if (jsonData != null) {
                    val jsonObject = JSONObject(jsonData)
                    val country = jsonObject.optString("country")
                    val city = jsonObject.optString("city")
                    val flagURL = jsonObject.optString("country_flag")

                    // Notify the callback with the obtained data
                    callback.onDataReceived(country, city, flagURL)
                } else {
                    // Handle empty response
                    callback.onError("Empty response")
                }
            }
        })
    }
    fun getPublicIPAddress(callback: (String) -> Unit, errorCallback: () -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://ipinfo.io")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure, for example, by calling the errorCallback
                errorCallback()
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                if (jsonData != null) {
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val ipAddress = jsonObject.optString("ip")
                        callback(ipAddress)
                    } catch (e: JSONException) {
                        errorCallback()
                    }
                } else {
                    errorCallback()
                }
            }
        })
    }


//    private fun checkUsagePermission(): Boolean {
//        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        var mode = 0
//        mode = appOps.checkOpNoThrow(
//            "android:get_usage_stats", Process.myUid(),
//            packageName
//        )
//        val granted = mode == AppOpsManager.MODE_ALLOWED
//        if (!granted) {
//            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//            startActivity(intent)
//            return false
//        }
//        return true
//    }



//    val networkUsage = NetworkUsageManager(this, Util.getSubscriberId(this))



//    val handler = Handler()
//    val runnableCode = object : Runnable {
//        override fun run() {
//            val now = networkUsage.getUsageNow(NetworkType.ALL)
//            val speeds = NetSpeed.calculateSpeed(now.timeTaken, now.downloads, now.uploads)
//            val todayM = networkUsage.getUsage(Interval.today, NetworkType.MOBILE)
//            val todayW = networkUsage.getUsage(Interval.today, NetworkType.WIFI)
//
//            binding.wifiUsagesTv.text = Util.formatData(todayW.downloads, todayW.uploads)[2]
//            binding.dataUsagesTv.text = Util.formatData(todayM.downloads, todayM.uploads)[2]
//            binding.apply {
//                totalSpeedTv.text = speeds[0].speed + "" + speeds[0].unit
//                downUsagesTv.text = "Down: " + speeds[1].speed + speeds[1].unit
//                upUsagesTv.text = "Up: " + speeds[2].speed + speeds[2].unit
//            }
//            handler.postDelayed(this, 1000)
//        }
//    }
//
//    runnableCode.run()
}