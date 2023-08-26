package com.xilli.stealthnet.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.TrafficStats
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentRateScreenBinding
import com.xilli.stealthnet.ui.menu.MenuFragment


class RateScreenFragment : Fragment() {
    private var binding: FragmentRateScreenBinding? = null
    private val mHandler = Handler()
    private var mStartRX: Long = 0
    private var mStartTX: Long = 0
    private var backPressedOnce = false
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var countdownValue = 4
    private var countDownTimer: CountDownTimer? = null
    private lateinit var originalDisconnectBackground: Drawable
    private var originalDisconnectTextColor: Int = Color.BLACK// Store the original text color



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRateScreenBinding.inflate(inflater, container, false)

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistner()
        setupBackPressedCallback()
        startRunnable()
        updateTrafficStats()

    }
    private fun startRunnable() {
        mRunnable.run()
    }
    private fun updateTrafficStats() {
        val resetDownload = TrafficStats.getTotalRxBytes()

        val rxBytes = TrafficStats.getTotalRxBytes() - mStartRX

        binding?.textView4?.text = rxBytes.toString()
        if (rxBytes >= 1024) {
            val rxKb = rxBytes / 1024
            binding?.textView4?.text = "$rxKb KBs"
            if (rxKb >= 1024) {
                val rxMB = rxKb / 1024
                binding?.textView4?.text = "$rxMB MBs"
                if (rxMB >= 1024) {
                    val rxGB = rxMB / 1024
                    binding?.textView4?.text = "$rxGB GBs"

                }
            }
        }

        mStartRX = resetDownload

        val resetUpload = TrafficStats.getTotalTxBytes()

        val txBytes = TrafficStats.getTotalTxBytes() - mStartTX

        binding?.uploaddata?.text = txBytes.toString()
        if (txBytes >= 1024) {
            val txKb = txBytes / 1024
            binding?.uploaddata?.text = "$txKb KBs"
            if (txKb >= 1024) {
                val txMB = txKb / 1024
                binding?.uploaddata?.text = "$txMB MBs"
                if (txMB >= 1024) {
                    val txGB = txMB / 1024
                    binding?.uploaddata?.text = "$txGB GBs"
                }
            }
        }

        mStartTX = resetUpload
    }

    private fun setupBackPressedCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!backPressedOnce) {
                    backPressedOnce = true
                    disconnectmethod()
                } else {
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun alertdialog(dialogInterface: DialogInterface) {
        val alertSheetDialog = dialogInterface as AlertDialog
        val alertdialog = alertSheetDialog.findViewById<View>(
            com.google.android.material.R.id.alertTitle
        )
            ?: return
        alertdialog.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun clicklistner() {
        binding?.menu?.setOnClickListener {
            val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.constraintlayoutmenu)
            drawerLayout.openDrawer(GravityCompat.START)
        }
        binding?.crosscancel?.setOnClickListener {
            disconnectmethod()

        }
        binding?.constraintLayout2?.setOnClickListener {
            val action = RateScreenFragmentDirections.actionRateScreenFragmentToServerListFragment()
            findNavController().navigate(action)
        }
        binding?.navigationView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings_menu -> {
                    findNavController().navigate(RateScreenFragmentDirections.actionRateScreenFragmentToSettingFragment())
                }
                R.id.server_menu -> {
                    findNavController().navigate(RateScreenFragmentDirections.actionRateScreenFragmentToServerListFragment())
                }
                R.id.split_menu -> {
                    findNavController().navigate(RateScreenFragmentDirections.actionRateScreenFragmentToSplitTunningFragment2())
                }
            }
            true
        }
    }

    private fun disconnectmethod() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cancel_vpn, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.TransparentAlertDialogTheme)
            .setView(dialogView)
            .create()
        alertdialog(dialog)
        dialog.show()

        val cancelTextView = dialogView.findViewById<TextView>(R.id.cancel)
        cancelTextView.setOnClickListener {
            dialog.dismiss()
        }

        val disconnectTextView = dialogView.findViewById<TextView>(R.id.disconnct)
        disconnectTextView.text = getString(R.string.disconnect_timer_initial)


        val originalDisconnectBackground = disconnectTextView.background
        val originalDisconnectTextColor = disconnectTextView.currentTextColor

        var remainingTime = countdownValue
        countDownTimer = object : CountDownTimer((countdownValue * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime--
                disconnectTextView.text = "Disconnect ($remainingTime s)"
                // Change the background color and text color here
                val disconnectViewColor = ContextCompat.getColor(requireContext(), R.color.disconnectview)
                disconnectTextView.setBackgroundResource(R.drawable.disconnect_timer_drawable)
                disconnectTextView.setTextColor(disconnectViewColor)// Example: Change text color to white
            }

            override fun onFinish() {
                // Countdown complete, navigate or perform your desired action
                disconnectTextView.setOnClickListener {
                    val action = RateScreenFragmentDirections.actionRateScreenFragmentToReportScreenFragment()
                    findNavController().navigate(action)
                    dialog.dismiss()
                }

                // Restore original background color and text color
                disconnectTextView.background = originalDisconnectBackground
                disconnectTextView.setTextColor(originalDisconnectTextColor)
                disconnectTextView.text = getString(R.string.disconnect_timer_initial)
            }
        }
        countDownTimer?.start()
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            updateTrafficStats()
            mHandler.postDelayed(this, 1000)
        }
    }
    override fun onDestroyView() {
        countDownTimer?.cancel()
        mHandler.removeCallbacks(mRunnable)
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
        super.onDestroyView()
    }
}