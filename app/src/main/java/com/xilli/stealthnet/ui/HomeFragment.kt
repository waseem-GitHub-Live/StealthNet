package com.xilli.stealthnet.ui

import android.animation.Animator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentHomeBinding
import com.xilli.stealthnet.ui.menu.MenuFragment


class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View? = binding?.root
        backgroundView()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistner()
    }

    private fun backgroundView() {
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Set the layout flags to enable immersive mode with transparent status bar
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            // Set the status bar background color to transparent
            requireActivity().window.statusBarColor = Color.TRANSPARENT
        }
    }
    private fun loadLottieAnimation() {
        val animationView = view?.findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        animationView?.setAnimation(R.raw.loading_animation)

        animationView?.addAnimatorUpdateListener {
            Log.d("LottieDebug", "Animation progress: ${animationView.progress}")
        }

        animationView?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {

            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }
        })

        animationView?.playAnimation()
    }

    private fun clicklistner() {
        binding?.menu?.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMenuFragment()
                findNavController().navigate(action)
        }
        binding?.connctlayout?.setOnClickListener {
            loadLottieAnimation()
            binding?.power?.visibility = View.GONE
            binding?.lottieAnimationView?.visibility = View.VISIBLE
            binding?.lottieAnimationView?.playAnimation()
            binding?.connect?.text = "Connecting"
            Handler().postDelayed({
                binding?.connect?.text = "Connect"
                binding?.power?.visibility = View.VISIBLE
                binding?.lottieAnimationView?.visibility = View.GONE
                val action = HomeFragmentDirections.actionHomeFragmentToRateScreenFragment()
                findNavController().navigate(action)
            }, 3000)
        }
    }


}