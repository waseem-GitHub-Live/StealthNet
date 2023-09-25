package com.xilli.stealthnet.helper

import Speed
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.onesignal.OneSignal
import com.xilli.stealthnet.R
import com.xilli.stealthnet.helper.Utility.showIP
import com.xilli.stealthnet.ui.HomeFragment.Companion.STATUS
import com.xilli.stealthnet.ui.ServerListFragment

abstract class ContentActivity : AppCompatActivity() {
    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0
    private var mLastTime: Long = 0
    private var mSpeed: Speed? = null

    var lottieAnimationView: LottieAnimationView? = null
    var vpnToastCheck = true
    var handlerTraffic: Handler? = null
    private val adCount = 0
    private var loadingAd: Boolean? = false
    var frameLayout: RelativeLayout? = null

    @JvmField


    var progressBarValue = 0
    var handler = Handler(Looper.getMainLooper())
    private val customHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L


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
    lateinit var sharedPreferences : SharedPreferences

    @JvmField
    var imgFlag: ImageView? = null

    @JvmField
    var flagName: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//
//        tvConnectionStatus = findViewById(R.id.connection_status)
//
//        ivConnectionStatusImage = findViewById(R.id.connection_status_image)

        ivVpnDetail = findViewById(R.id.constraintLayout2details)

//        timerTextView = findViewById(R.id.tv_timer)

        connectBtnTextView = findViewById(R.id.imageView4)

        imgFlag = findViewById(R.id.flagimageView)

//        rcvFree = findViewById(R.id.rcv_free)


        flagName = findViewById(R.id.flag_name)

//        footer = findViewById(R.id.footer)

//        frameLayout = findViewById(R.id.fl_adplaceholder)

        connectBtnTextView?.setOnClickListener {
            btnConnectDisconnect()
        }


        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        tvIpAddress = findViewById<TextView>(R.id.vpn_ip)
        showIP()


        ivVpnDetail?.setOnClickListener {
            if(Constants.FREE_SERVERS != "server" && Constants.PREMIUM_SERVERS != "")
                showServerList()
            else
                Toast.makeText(applicationContext, "Loading Server......", Toast.LENGTH_SHORT).show()
        }

    }
    private fun showServerList() {
        startActivity(Intent(this, ServerListFragment::class.java))
    }
    private fun btnConnectDisconnect() {
        if (ContactsContract.ProviderStatus.STATUS != "DISCONNECTED") {
            disconnectAlert()
        } else {
            if (!Utility.isOnline(applicationContext)) {
                Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            } else {
                checkSelectedCountry()
            }
        }
    }
    protected fun disconnectAlert() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(applicationContext)
        builder.setTitle("Do you want to disconnect?")
        builder.setPositiveButton(
            "Disconnect"
        ) { _, _ ->
            disconnectFromVpn()
            STATUS = "DISCONNECTED"

            Toast.makeText(applicationContext, "Server Disconnected", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ ->
            Toast.makeText(applicationContext, "VPN Remains Connected", Toast.LENGTH_SHORT).show()

        }
        builder.show()
    }
    fun updateUI(status:String) {

        when (status) {
            "CONNECTED" -> {
                STATUS = "CONNECTED"
                Utility.textDownloading!!.visibility = View.VISIBLE
                Utility.textUploading!!.visibility = View.VISIBLE
                Utility.connectBtnTextView!!.isEnabled = true
                Utility.connectionStateTextView!!.setText(R.string.connected)
                Utility.timerTextView!!.visibility = View.GONE
                showIP()
                Utility.connectBtnTextView!!.visibility = View.VISIBLE
                Utility.tvConnectionStatus!!.text = "Selected"
                Utility.showToast("VPN Remains Connected")
            }
            "AUTH" -> {
                STATUS = "AUTHENTICATION"
                Utility.connectBtnTextView!!.visibility = View.VISIBLE
                Utility.connectionStateTextView!!.setText(R.string.auth)
                Utility.connectBtnTextView!!.isEnabled = true
                Utility.timerTextView!!.visibility = View.GONE
            }
            "WAIT" -> {
                STATUS = "WAITING"
                Utility.connectBtnTextView!!.visibility = View.VISIBLE
                Utility.connectionStateTextView!!.setText(R.string.wait)
                Utility.connectBtnTextView!!.isEnabled = true
                Utility.timerTextView!!.visibility = View.GONE
            }
            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                Utility.connectBtnTextView!!.visibility = View.VISIBLE
                Utility.connectionStateTextView!!.setText(R.string.recon)
                Utility.connectBtnTextView!!.isEnabled = true
                Utility.timerTextView!!.visibility = View.GONE
            }
            "LOAD" -> {
                STATUS = "LOAD"
                Utility.connectBtnTextView!!.visibility = View.VISIBLE

                Utility.connectionStateTextView!!.setText(R.string.connecting)
                Utility.connectBtnTextView!!.isEnabled = true
                Utility.timerTextView!!.visibility = View.GONE
            }
            "ASSIGN_IP" -> {
                STATUS = "LOAD"
                Utility.connectBtnTextView!!.visibility = View.VISIBLE

                Utility.connectionStateTextView!!.setText(R.string.assign_ip)
                Utility.connectBtnTextView!!.isEnabled = true
                Utility.timerTextView!!.visibility = View.GONE
            }
            "GET_CONFIG" -> {
                STATUS = "LOAD"
                Utility.connectBtnTextView!!.visibility = View.VISIBLE

                Utility.connectionStateTextView!!.setText(R.string.config)
                Utility.connectBtnTextView!!.isEnabled = true
                Utility.timerTextView!!.visibility = View.GONE
            }
            "USERPAUSE" -> {
                STATUS = "DISCONNECTED"
                Utility.tvConnectionStatus!!.text = "Not Selected"
                Utility.tvConnectionStatus!!.text = "Not Selected"
            }
            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
                Utility.tvConnectionStatus!!.text = "Not Selected"
                showIP()

                Utility.tvConnectionStatus!!.text = "Not Selected"
            }
            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
                Utility.tvConnectionStatus!!.text = "Not Selected"
                Utility.timerTextView!!.visibility = View.INVISIBLE
                showIP()
                Utility.tvConnectionStatus!!.text = "Not Selected"
            }
        }
    }
    protected abstract fun disconnectFromVpn()
    protected abstract fun checkSelectedCountry()
}
