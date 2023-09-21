package com.xilli.stealthnet.data

data class VpnServerConfiguration(
    val name: String,
    val ipAddress: String,
    val country: String,
    val flagResource: Int,
    val serverType: ServerType
)
