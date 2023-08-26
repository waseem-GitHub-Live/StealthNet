package com.xilli.stealthnet.ui

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.navigation.NavigationView
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadLottieAnimation()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistner()
    }

    private fun loadLottieAnimation() {
        binding?.lottieAnimationView?.setAnimation(R.raw.loading_animation)
        binding?.lottieAnimationView2?.setAnimation(R.raw.backview)
        binding?.lottieAnimationView2?.repeatCount = LottieDrawable.INFINITE
        binding?.lottieAnimationView?.addAnimatorUpdateListener {
        }
        binding?.lottieAnimationView2?.addAnimatorUpdateListener {
        }
        binding?.lottieAnimationView?.playAnimation()
        binding?.lottieAnimationView2?.playAnimation()
    }


    private fun clicklistner() {
        binding?.menu?.setOnClickListener {
            val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.constraintlayoutmenu)
            drawerLayout.openDrawer(GravityCompat.START)
        }
        binding?.imageView4?.setOnClickListener {
            loadLottieAnimation()
            binding?.power?.visibility = View.GONE
            binding?.lottieAnimationView?.visibility = View.VISIBLE
            binding?.connect?.text = "Connecting"
            Handler().postDelayed({
                val action = HomeFragmentDirections.actionHomeFragmentToRateScreenFragment()
                findNavController().navigate(action)
            }, 3000)
        }

        binding?.constraintLayout2?.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToServerListFragment()
            findNavController().navigate(action)
        }
        binding?.navigationView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings_menu -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingFragment())
                }
                R.id.server_menu -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToServerListFragment())
                }
                R.id.split_menu -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSplitTunningFragment())
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_menu) // Replace with your drawer icon
            setDisplayHomeAsUpEnabled(true)
        }
        loadLottieAnimation()
    }
}