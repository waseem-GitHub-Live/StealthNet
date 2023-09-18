package com.xilli.stealthnet.ui.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.xilli.stealthnet.R
import com.xilli.stealthnet.helper.AppHelper
import com.xilli.stealthnet.helper.VpnServiceCallback

class VpnServices: VpnService() {

    private lateinit var vpnInterface: ParcelFileDescriptor
    companion object {
        const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "vpn_channel"
    }
    private var startTime: Long = 0
    private val elapsedTimeHandler = Handler(Looper.getMainLooper())
    private lateinit var elapsedTimeRunnable: Runnable
    private val handler = Handler(Looper.getMainLooper())
    private var vpnCallback: VpnServiceCallback? = null

    fun setVpnServiceCallback(callback: VpnServiceCallback,elapsedTimeListener: VpnServiceCallback) {
        vpnCallback = callback
        this.vpnCallback = elapsedTimeListener
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Configure VPN parameters here
        if (intent?.action == "stop_vpn") {
            stopVpnService()
            return START_NOT_STICKY
        }
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        startTime = sharedPreferences.getLong("startTime", 0)
        vpnCallback?.onVpnServiceStarted()
        startTime = System.currentTimeMillis()
        val builder = Builder()
            .setSession("MyVPNService")
            .addAddress("10.0.0.1", 32) // Example IP address and prefix length
            .addDnsServer("8.8.8.8")
        // Add more configurations as needed
        vpnInterface = builder.establish()!!
        AppHelper.vpnService = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForeground(NOTIFICATION_ID, createNotification())
        }

        elapsedTimeRunnable = Runnable {
            updateElapsedTime()
            elapsedTimeHandler.postDelayed(elapsedTimeRunnable, 1000)
        }
        elapsedTimeHandler.post(elapsedTimeRunnable)

        return START_STICKY
    }

    private fun updateElapsedTime() {
        val currentTime = System.currentTimeMillis()
        val elapsedTimeInMillis = currentTime - startTime
        val elapsedSeconds = elapsedTimeInMillis / 1000
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60

        val elapsedTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        // Send the elapsed time to the fragment using a broadcast
        val intent = Intent("com.xilli.stealthnet.elapsedTime")
        intent.putExtra("elapsedTime", elapsedTimeString)
        sendBroadcast(intent)

        // Update the startTime in SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("startTime", startTime)
        editor.apply()
    }

    fun updateStartTime(newStartTime: Long) {
        startTime = newStartTime
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VPN Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        // Create an intent to stop the VPN service when notification is clicked
        val stopIntent = Intent(this, VpnServices::class.java)
        stopIntent.action = "stop_vpn"
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Create and return a notification for the foreground service
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VPN Service")
            .setContentText("VPN is running")
            .setSmallIcon(R.drawable.ic_notifications)
            .addAction(R.drawable.ic_notifications, "Stop", pendingStopIntent)
            .build()

        return notification
    }


    override fun onDestroy() {
        if (::elapsedTimeRunnable.isInitialized) {
            handler.removeCallbacks(elapsedTimeRunnable)
        }
        stopVpnService()
        Log.d("vpn", "VPN service onDestroy")
        super.onDestroy()
    }

    private fun stopVpnService() {
        // Clean up VPN resources and release the interface
        vpnInterface.close()
        vpnCallback = null
        stopForeground(true)
        stopSelf()
        vpnCallback?.onVpnServiceStopped()
        Log.d("vpn", "VPN service stoppppppppppppppppppp")
    }
    fun startVpn() {
        // Start VPN logic here

        // Notify the callback that the VPN service has started
        vpnCallback?.onVpnServiceStarted()
    }

    private fun stopVpn() {
        // Stop VPN logic here

        // Notify the callback that the VPN service has stopped
        vpnCallback?.onVpnServiceStopped()
    }
}