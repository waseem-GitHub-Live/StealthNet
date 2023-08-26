package com.xilli.stealthnet

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.xilli.stealthnet.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.lottieAnimationViewsplash.setAnimation(R.raw.loading_animation)
        installSplashScreen()

        binding.lottieAnimationViewsplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {

                startActivity(MainActivity.newIntent(this@SplashActivity))
                finish()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        binding.lottieAnimationViewsplash.playAnimation()
    }
}