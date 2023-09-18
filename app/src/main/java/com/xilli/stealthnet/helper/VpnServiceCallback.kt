package com.xilli.stealthnet.helper

interface VpnServiceCallback {
    fun onElapsedTimeUpdated(elapsedTimeString: String)
    fun onVpnServiceStarted()
    fun onVpnServiceStopped()
}