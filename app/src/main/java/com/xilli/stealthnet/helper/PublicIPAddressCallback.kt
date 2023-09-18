package com.xilli.stealthnet.helper

interface PublicIPAddressCallback {
    fun onPublicIPAddressReceived(ipAddress: String)
    fun onPublicIPAddressError(error: String)
}