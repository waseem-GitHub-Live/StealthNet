package com.xilli.stealthnet.helper

import android.content.Context
import android.content.SharedPreferences

class ActiveServer {
    fun saveServer(countries: Countries, context: Context) {
        val sp = context.applicationContext
            .getSharedPreferences("activeServer", 0)
        val editor: SharedPreferences.Editor
        editor = sp.edit()
        editor.putString("countryName", countries.getCountry1())
        editor.putString("vpnUserName", countries.getOvpnUserName1())
        editor.putString("vpnPassword", countries.getOvpnUserPassword1())
        editor.putString("config", countries.getOvpn1())
        editor.putString("flagUrl", countries.getFlagUrl1())
        editor.commit()
    }

    fun getSavedServer(context: Context): Countries? {
        val sp = context.applicationContext
            .getSharedPreferences("activeServer", 0)
        return Countries(
            sp.getString("countryName", ""),
            sp.getString("flagUrl", ""),
            sp.getString("config", ""),
            sp.getString("vpnUserName", ""),
            sp.getString("vpnPassword", "")
        )
    }

    fun deleteSaveServer(key: String?, context: Context) {
        val sp = context.applicationContext
            .getSharedPreferences("activeServer", 0)
        val editor: SharedPreferences.Editor
        editor = sp.edit()
        editor.remove(key)
        editor.commit()
    }
}