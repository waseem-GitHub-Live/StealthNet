package com.xilli.stealthnet.helper

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Settings
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity

object AppHelper {

//    private fun checkUsagePermission(): Boolean {
//        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        var mode = 0
//        mode = appOps.checkOpNoThrow(
//            "android:get_usage_stats", Process.myUid(),
//            packageName
//        )
//        val granted = mode == AppOpsManager.MODE_ALLOWED
//        if (!granted) {
//            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//            startActivity(intent)
//            return false
//        }
//        return true
//    }



//    val networkUsage = NetworkUsageManager(this, Util.getSubscriberId(this))



//    val handler = Handler()
//    val runnableCode = object : Runnable {
//        override fun run() {
//            val now = networkUsage.getUsageNow(NetworkType.ALL)
//            val speeds = NetSpeed.calculateSpeed(now.timeTaken, now.downloads, now.uploads)
//            val todayM = networkUsage.getUsage(Interval.today, NetworkType.MOBILE)
//            val todayW = networkUsage.getUsage(Interval.today, NetworkType.WIFI)
//
//            binding.wifiUsagesTv.text = Util.formatData(todayW.downloads, todayW.uploads)[2]
//            binding.dataUsagesTv.text = Util.formatData(todayM.downloads, todayM.uploads)[2]
//            binding.apply {
//                totalSpeedTv.text = speeds[0].speed + "" + speeds[0].unit
//                downUsagesTv.text = "Down: " + speeds[1].speed + speeds[1].unit
//                upUsagesTv.text = "Up: " + speeds[2].speed + speeds[2].unit
//            }
//            handler.postDelayed(this, 1000)
//        }
//    }
//
//    runnableCode.run()
}