package com.xilli.stealthnet.helper

interface GeolocationCallback {
    fun onDataReceived(country: String, city: String, flagURL: String)
    fun onError(error: String)
}