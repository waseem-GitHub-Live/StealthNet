package com.xilli.stealthnet.helper

import android.os.Parcel
import android.os.Parcelable

class Countries : Parcelable {
    var country: String? = null
    var flagUrl: String? = null
    var ovpn: String? = null
    var ovpnUserName: String? = null
    var ovpnUserPassword: String? = null
    var signal: Int = 0 // Add signal property
    var crown: Int = 0 // Add crown property

    constructor() {}

    constructor(country: String?, flagUrl: String?, ovpn: String?) {
        this.country = country
        this.flagUrl = flagUrl
        this.ovpn = ovpn
    }

    constructor(country: String?, flagUrl: String?, ovpn: String?, ovpnUserName: String?, ovpnUserPassword: String?) {
        this.country = country
        this.flagUrl = flagUrl
        this.ovpn = ovpn
        this.ovpnUserName = ovpnUserName
        this.ovpnUserPassword = ovpnUserPassword
    }

    protected constructor(`in`: Parcel) {
        country = `in`.readString()
        flagUrl = `in`.readString()
        ovpn = `in`.readString()
        ovpnUserName = `in`.readString()
        ovpnUserPassword = `in`.readString()
        signal = `in`.readInt() // Read as Int
        crown = `in`.readInt() // Read as Int
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(country)
        dest.writeString(flagUrl)
        dest.writeString(ovpn)
        dest.writeString(ovpnUserName)
        dest.writeString(ovpnUserPassword)
    }

    companion object CREATOR : Parcelable.Creator<Countries> {
        override fun createFromParcel(parcel: Parcel): Countries {
            return Countries(parcel)
        }

        override fun newArray(size: Int): Array<Countries?> {
            return arrayOfNulls(size)
        }
    }
}
