package com.xilli.stealthnet.ui.services

import android.content.Context
import android.content.SharedPreferences
import android.os.ParcelFileDescriptor
import com.xilli.stealthnet.data.VpnServerConfiguration
import com.xilli.stealthnet.helper.VpnServiceCallback

class VpnServiceHandler(
    private val context: Context,
    private val vpnCallback: VpnServiceCallback,
    private val vpnServerConfigurations: List<VpnServerConfiguration>
) {

    private lateinit var vpnInterface: ParcelFileDescriptor
    private lateinit var selectedServer: VpnServerConfiguration

    fun setSelectedServer(serverIndex: Int) {
        if (serverIndex in 0 until vpnServerConfigurations.size) {
            selectedServer = vpnServerConfigurations[serverIndex]
            val editor = getSharedPreferences().edit()
            editor.putInt("selectedServerIndex", serverIndex)
            editor.apply()
            stopVpnService()
            startVpn()
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    private fun startVpn() {
        // Implement your VPN start logic here
        // You may need to pass additional data or use the selectedServer configuration.
         vpnCallback.onVpnServiceStarted()
    }

    private fun stopVpnService() {
        // Implement your VPN service stopping logic here
        // This may involve stopping the VPN tunnel and releasing resources.
         vpnCallback.onVpnServiceStopped()
    }
}
