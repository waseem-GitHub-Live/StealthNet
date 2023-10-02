package com.xilli.stealthnet.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings.Global.getString
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.xilli.stealthnet.R
import java.net.Inet4Address
import java.net.NetworkInterface

object Utility {
    private const val TAG = "Utility"
    var tvIpAddress: TextView? = null
    var textDownloading: TextView? = null
    var textUploading: TextView? = null
    var tvConnectionStatus: TextView? = null
    var ivConnectionStatusImage: ImageView? = null
    var ivVpnDetail: ImageView? = null
    var timerTextView: TextView? = null
    var connectBtnTextView: ImageView? = null
    var connectionStateTextView: TextView? = null
    var rcvFree: RecyclerView? = null
    var footer: RelativeLayout? = null
    private var appContext: Context? = null
    var countryName: String? = null
    var flagUrl: String? = null
    private var STATUS: String? = "DISCONNECTED"
// fun for check online devices
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
// Ip address method to get from internet
    fun showIP(textView: TextView) {
        val queue = Volley.newRequestQueue(appContext)
        val urlip = "https://checkip.amazonaws.com/"

        val stringRequest = StringRequest(
            Request.Method.GET, urlip,
            Response.Listener<String> { response ->
                textView.text = response
            },
            Response.ErrorListener { _ ->
                textView.text = getIpv4HostAddress()
            }
        )

        queue.add(stringRequest)
    }

    private fun getIpv4HostAddress(): CharSequence? {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }

    fun initialize(context: Context, view: View) {
        appContext = context.applicationContext
        connectionStateTextView = view.findViewById(R.id.connect)
        connectBtnTextView = view.findViewById(R.id.imageView4)
        tvIpAddress = view.findViewById(R.id.ip)
    }

    fun showToast(message: String) {
        appContext?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    // it tells the that situation of vpn its connected or not or what whats its doing and upgrade UI
    fun updateUI(status:String) {

        when (status) {
            "CONNECTED" -> {
                STATUS = "CONNECTED"
                textDownloading?.visibility = View.VISIBLE
                textUploading?.visibility = View.VISIBLE
                connectBtnTextView?.isEnabled = true
                connectionStateTextView?.setText(R.string.connected)
                timerTextView?.visibility = View.GONE
                tvIpAddress?.let { showIP(it) }
                connectBtnTextView?.visibility = View.VISIBLE
                tvConnectionStatus?.text = "Selected"
                showToast("VPN Remains Connected")

            }
            "AUTH" -> {
                STATUS = "AUTHENTICATION"
                connectBtnTextView?.visibility = View.VISIBLE
                connectionStateTextView?.setText(R.string.auth)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
                showToast("VPN AUTHENTICATION")
            }
            "WAIT" -> {
                STATUS = "WAITING"
                connectBtnTextView?.visibility = View.VISIBLE
                connectionStateTextView?.setText(R.string.wait)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
                showToast("VPN WAITING")
            }
            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                connectBtnTextView?.visibility = View.VISIBLE
                connectionStateTextView?.setText(R.string.recon)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
                showToast("VPN RECONNECTING")
            }
            "LOAD" -> {
                STATUS = "LOAD"
                connectBtnTextView?.visibility = View.VISIBLE
                connectionStateTextView?.setText(R.string.connecting)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
            }
            "ASSIGN_IP" -> {
                STATUS = "LOAD"
                connectBtnTextView?.visibility = View.VISIBLE

                connectionStateTextView?.setText(R.string.assign_ip)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
                showToast("VPN ASSIGN_IP")
            }
            "GET_CONFIG" -> {
                STATUS = "LOAD"
                connectBtnTextView?.visibility = View.VISIBLE

                connectionStateTextView?.setText(R.string.config)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
                showToast("VPN GET_CONFIG")
            }
            "USERPAUSE" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus?.text = "Not Selected"
                tvConnectionStatus?.text = "Not Selected"
                showToast("VPN DISCONNECTED USERPAUSE")
            }
            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus?.text = "Not Selected"
                tvIpAddress?.let { showIP(it) }
                tvConnectionStatus?.text = "Not Selected"
                showToast("VPN DISCONNECTED NONETWORK")
            }
            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus?.text = "Not Selected"
                tvIpAddress?.let { showIP(it) }
            }
        }
    }

}