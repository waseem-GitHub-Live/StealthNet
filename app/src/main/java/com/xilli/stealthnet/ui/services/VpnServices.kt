//package com.xilli.stealthnet.ui.services
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.net.VpnService
//import android.os.Build
//import android.os.Handler
//import android.os.Looper
//import android.os.ParcelFileDescriptor
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.xilli.stealthnet.R
//import com.xilli.stealthnet.data.ServerType
//import com.xilli.stealthnet.data.VpnServerConfiguration
//import com.xilli.stealthnet.helper.AppHelper
//import com.xilli.stealthnet.helper.VpnServiceCallback
//
//class VpnServices: VpnService() {
//
//    private lateinit var vpnInterface: ParcelFileDescriptor
//    companion object {
//        const val NOTIFICATION_ID = 1
//        private const val CHANNEL_ID = "vpn_channel"
//    }
//    private var startTime: Long = 0
//    private val elapsedTimeHandler = Handler(Looper.getMainLooper())
//    private lateinit var elapsedTimeRunnable: Runnable
//    private val handler = Handler(Looper.getMainLooper())
//    private var vpnCallback: VpnServiceCallback? = null
//    private val vpnServerConfigurations = listOf(
//        VpnServerConfiguration("Server 1 (VIP)", "10.0.0.1", "Country 1", R.drawable.flag, ServerType.VIP),
//        VpnServerConfiguration("Server 2 (VIP)", "10.0.0.2", "Country 2", R.drawable.flag, ServerType.VIP),
//        VpnServerConfiguration("Server 3 (Free)", "10.0.0.3", "Country 3", R.drawable.flag, ServerType.FREE),
//        VpnServerConfiguration("Server 4 (Free)", "10.0.0.4", "Country 4", R.drawable.flag, ServerType.FREE),
//        VpnServerConfiguration("Default Server", "10.0.0.5", "Country 5", R.drawable.flag, ServerType.Default)
//    )
//
//    private lateinit var selectedServer: VpnServerConfiguration
//    fun setVpnServiceCallback(callback: VpnServiceCallback,elapsedTimeListener: VpnServiceCallback) {
//        vpnCallback = callback
//        this.vpnCallback = elapsedTimeListener
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        // Configure VPN parameters here
//        if (intent?.action == "stop_vpn") {
//            stopVpnService()
//            return START_NOT_STICKY
//        }
//
//        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val serverIndex = sharedPreferences.getInt("selectedServerIndex", -1)
//        setSelectedServer(serverIndex, getSharedPreferences("MyPrefs", Context.MODE_PRIVATE))
//        if (serverIndex in 0 until vpnServerConfigurations.size) {
//            selectedServer = vpnServerConfigurations[serverIndex]
//        } else {
//            // Use the default server if no server is selected
//            selectedServer = vpnServerConfigurations.last()
//        }
//
//        startTime = sharedPreferences.getLong("startTime", 0)
//        vpnCallback?.onVpnServiceStarted()
//        startTime = System.currentTimeMillis()
//
//        val builder = Builder()
//            .setSession("MyVPNService")
//            .addAddress(selectedServer.ipAddress, 32)
//            .addDnsServer("8.8.8.8")
//
//        vpnInterface = builder.establish()!!
//        AppHelper.vpnService = this
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel()
//            startForeground(NOTIFICATION_ID, createNotification())
//        }
//
//        elapsedTimeRunnable = Runnable {
//            updateElapsedTime()
//            elapsedTimeHandler.postDelayed(elapsedTimeRunnable, 1000)
//        }
//        elapsedTimeHandler.post(elapsedTimeRunnable)
//
//        return START_STICKY
//    }
//    fun setSelectedServer(serverIndex: Int, sharedPreferences: SharedPreferences) {
//        if (serverIndex in 0 until vpnServerConfigurations.size) {
//            selectedServer = vpnServerConfigurations[serverIndex]
//            val editor = sharedPreferences.edit()
//            editor.putInt("selectedServerIndex", serverIndex)
//            editor.apply()
//            stopVpnService()
//            startVpn()
//        }
//    }
//
//    private fun updateElapsedTime() {
//        val currentTime = System.currentTimeMillis()
//        val elapsedTimeInMillis = currentTime - startTime
//        val elapsedSeconds = elapsedTimeInMillis / 1000
//        val hours = elapsedSeconds / 3600
//        val minutes = (elapsedSeconds % 3600) / 60
//        val seconds = elapsedSeconds % 60
//
//        val elapsedTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
//
//        // Send the elapsed time to the fragment using a broadcast
//        val intent = Intent("com.xilli.stealthnet.elapsedTime")
//        intent.putExtra("elapsedTime", elapsedTimeString)
//        sendBroadcast(intent)
//
//        // Update the startTime in SharedPreferences
//        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putLong("startTime", startTime)
//        editor.apply()
//    }
//
//    fun updateStartTime(newStartTime: Long) {
//        startTime = newStartTime
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "VPN Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    private fun createNotification(): Notification {
//        // Create an intent to stop the VPN service when notification is clicked
//        val stopIntent = Intent(this, VpnServices::class.java)
//        stopIntent.action = "stop_vpn"
//        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        // Create and return a notification for the foreground service
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("VPN Service")
//            .setContentText("VPN is running")
//            .setSmallIcon(R.drawable.ic_notifications)
//            .addAction(R.drawable.ic_notifications, "Stop", pendingStopIntent)
//            .build()
//
//        return notification
//    }
//
//
//    override fun onDestroy() {
//        if (::elapsedTimeRunnable.isInitialized) {
//            handler.removeCallbacks(elapsedTimeRunnable)
//        }
//        stopVpnService()
//        Log.d("vpn", "VPN service onDestroy")
//        super.onDestroy()
//    }
//
//    private fun stopVpnService() {
//        // Clean up VPN resources and release the interface
//        if (::vpnInterface.isInitialized) {
//            // Clean up VPN resources and release the interface
//            vpnInterface.close()
//        }
//        vpnCallback = null
//        stopForeground(true)
//        stopSelf()
//        vpnCallback?.onVpnServiceStopped()
//        Log.d("vpn", "VPN service stoppppppppppppppppppp")
//    }
//    fun startVpn() {
//        // Start VPN logic here
//
//        // Notify the callback that the VPN service has started
//        vpnCallback?.onVpnServiceStarted()
//    }
//
//    private fun stopVpn() {
//        // Stop VPN logic here
//
//        // Notify the callback that the VPN service has stopped
//        vpnCallback?.onVpnServiceStopped()
//    }
//}