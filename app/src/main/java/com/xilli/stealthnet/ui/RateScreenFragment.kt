package com.xilli.stealthnet.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentRateScreenBinding
import com.xilli.stealthnet.ui.menu.MenuFragment


class RateScreenFragment : Fragment() {
    private var binding: FragmentRateScreenBinding? = null
    private val mHandler = Handler()
    private var mStartRX: Long = 0
    private var mStartTX: Long = 0


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
        mRunnable.run()
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
            val action = RateScreenFragmentDirections.actionRateScreenFragmentToMenuFragment()
            findNavController().navigate(action)
        }
        binding?.crosscancel?.setOnClickListener {
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
            disconnectTextView.setOnClickListener {
                val action = RateScreenFragmentDirections.actionRateScreenFragmentToReportScreenFragment()
                findNavController().navigate(action)
                dialog.dismiss()
            }
        }
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
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

            mHandler.postDelayed(this, 1000)
        }
    }
}