package com.xilli.stealthnet.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.onesignal.OneSignal
import com.xilli.stealthnet.R

class MainActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OneSignal.initWithContext(this)
        OneSignal.setAppId("a2be7720-a32b-415a-9db1-d50fdc54f069")
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Check if onboarding is completed
        val sharedPreferences = getSharedPreferences("onboarding", MODE_PRIVATE)
        val onboardingCompleted = sharedPreferences.getBoolean("completed", false)

        if (!onboardingCompleted) {
            // Navigate to the OnboardingFragment
            navController.navigate(R.id.onboardingScreenFragment)
        } else {
            // Navigate to the HomeFragment
            navController.navigate(R.id.homeFragment)
        }
    }
}