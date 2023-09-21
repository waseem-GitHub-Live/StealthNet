package com.xilli.stealthnet.ui

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentHomeBinding
import com.xilli.stealthnet.helper.VpnServiceCallback
import com.xilli.stealthnet.ui.services.VpnServices
import com.xilli.stealthnet.ui.viewmodels.VpnViewModel

class HomeFragment : Fragment(), VpnServiceCallback {
    private var binding: FragmentHomeBinding? = null
    private var vpnService: VpnServices? = null
    private lateinit var viewModel: VpnViewModel
    var vpnInterface: ParcelFileDescriptor? = null
    private val VPN_REQUEST_CODE = 123
    private var isVpnStarted = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadLottieAnimation()
        viewModel = ViewModelProvider(requireActivity())[VpnViewModel::class.java]
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistner()
        backexitclick()
    }

    private fun backexitclick() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                showAlertDialog()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun showAlertDialog() {
        val alertDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_exit, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setView(alertDialogView)

        val exitButton = alertDialogView.findViewById<ConstraintLayout>(R.id.exitbutton)
        val cancelButton = alertDialogView.findViewById<ConstraintLayout>(R.id.cancelbutton2)

        exitButton.setOnClickListener {
            activity?.finish()
        }
        val alertDialog = alertDialogBuilder.create()
        cancelButton.setOnClickListener {

            alertDialog.dismiss()
        }

        val dialogWindow = alertDialog.window
        dialogWindow?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog.show()
    }



//    private fun alertdialogexit(dialogInterface: DialogInterface) {
//        val alertSheetDialog = dialogInterface as AlertDialog
//        val alertdialog = alertSheetDialog.findViewById<View>(
//            com.google.android.material.R.id.alertTitle
//        )
//            ?: return
//        alertdialog.setBackgroundColor(Color.TRANSPARENT)
//    }

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
            startVpn()
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
    private fun startVpn() {
        val intent = VpnService.prepare(context)
        if (intent != null) {
            startActivityForResult(intent, VPN_REQUEST_CODE)
        } else {
            startVpnService()
        }
    }

    private fun startVpnService() {
        val vpnIntent = Intent(requireContext(), VpnServices::class.java)
        ContextCompat.startForegroundService(requireContext(), vpnIntent)
        isVpnStarted = true
        Log.d("vpn", "VPN service start")
        val service = vpnIntent.component?.className
        if (service == VpnServices::class.java.name) {
            val vpnService = vpnIntent as? VpnServices
            vpnService?.setVpnServiceCallback(this,this)
        }
    }

    override fun onElapsedTimeUpdated(elapsedTimeString: String) {

    }

    override fun onVpnServiceStarted() {
        val vpnIntent = Intent(requireContext(), VpnServices::class.java)
        ContextCompat.startForegroundService(requireContext(), vpnIntent)
        isVpnStarted = true

        val service = vpnIntent.component?.className
        if (service == VpnServices::class.java.name) {
            val vpnService = vpnIntent as? VpnServices

            // Start the VPN service first
            vpnService?.startVpn()

            // Set the callback
            vpnService?.setVpnServiceCallback(this, this)
        }
    }



    override fun onVpnServiceStopped() {
        val vpnIntent = Intent(requireContext(), VpnServices::class.java)
        requireContext().stopService(vpnIntent)

        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(VpnServices.NOTIFICATION_ID)
        isVpnStarted = false
    }

    private fun alertdialog(dialogInterface: DialogInterface) {
        val alertSheetDialog = dialogInterface as AlertDialog
        val alertdialog = alertSheetDialog.findViewById<View>(
            com.google.android.material.R.id.alertTitle
        )
            ?: return
        alertdialog.setBackgroundColor(Color.TRANSPARENT)
    }



}