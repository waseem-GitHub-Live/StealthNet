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
import com.xilli.stealthnet.ui.HomeFragment.Companion.STATUS

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
     fun showIP() {
        val queue = Volley.newRequestQueue(appContext)
        val urlip = "https://checkip.amazonaws.com/"

        val stringRequest = StringRequest(
            Request.Method.GET, urlip,
            Response.Listener<String> { response ->
                tvIpAddress?.text = response
            },
            Response.ErrorListener { error ->
                tvIpAddress?.text = appContext?.getString(R.string.name_app)
            }
        )

        queue.add(stringRequest)
    }

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    fun showToast(message: String) {
        appContext?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
    fun updateUI(status:String) {

        when (status) {
            "CONNECTED" -> {
                STATUS = "CONNECTED"
                textDownloading?.visibility = View.VISIBLE
                textUploading?.visibility = View.VISIBLE
                connectBtnTextView?.isEnabled = true
                connectionStateTextView?.setText(R.string.connected)
                timerTextView?.visibility = View.GONE
                showIP()
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
            }
            "WAIT" -> {
                STATUS = "WAITING"
                connectBtnTextView?.visibility = View.VISIBLE
                connectionStateTextView?.setText(R.string.wait)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
            }
            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                connectBtnTextView?.visibility = View.VISIBLE
                connectionStateTextView?.setText(R.string.recon)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
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
            }
            "GET_CONFIG" -> {
                STATUS = "LOAD"
                connectBtnTextView?.visibility = View.VISIBLE

                connectionStateTextView?.setText(R.string.config)
                connectBtnTextView?.isEnabled = true
                timerTextView?.visibility = View.GONE
            }
            "USERPAUSE" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus?.text = "Not Selected"
                tvConnectionStatus?.text = "Not Selected"
            }
            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus?.text = "Not Selected"
                showIP()

                tvConnectionStatus?.text = "Not Selected"
            }
            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus?.text = "Not Selected"
                timerTextView?.visibility = View.INVISIBLE
                showIP()
                tvConnectionStatus?.text = "Not Selected"
            }
        }
    }

}