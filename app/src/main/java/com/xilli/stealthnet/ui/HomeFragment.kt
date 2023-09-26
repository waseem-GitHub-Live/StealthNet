package com.xilli.stealthnet.ui

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentHomeBinding
import com.xilli.stealthnet.helper.ActiveServer
import com.xilli.stealthnet.helper.ContentActivity
import com.xilli.stealthnet.helper.Countries
import com.xilli.stealthnet.helper.Utility
import com.xilli.stealthnet.helper.Utility.textDownloading
import com.xilli.stealthnet.helper.Utility.textUploading
import com.xilli.stealthnet.helper.Utility.updateUI
import com.xilli.stealthnet.ui.viewmodels.VpnViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.oneconnectapi.app.OpenVpnApi
import top.oneconnectapi.app.core.OpenVPNThread

class HomeFragment : Fragment(){
     private var binding: FragmentHomeBinding? = null
    private lateinit var viewModel: VpnViewModel
    var selectedCountry: Countries? = null
    private var isFirst = true
    private var connectBtnTextView: ImageView? = null
    private var connectionStateTextView: TextView? = null
    private var timerTextView: TextView? = null
    private var isButtonClicked = true
    private var isNavigationInProgress = false
    private val VPN_PERMISSION_REQUEST_CODE = 123
    companion object {
        var type = ""
        val activeServer = ActiveServer()
        var STATUS = "DISCONNECTED"

    }
    @JvmField
    var flagName: TextView? = null

    @JvmField
    var imgFlag: ImageView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadLottieAnimation()
        viewModel = ViewModelProvider(requireActivity())[VpnViewModel::class.java]
        binding?.lifecycleOwner = viewLifecycleOwner
        connectionStateTextView = binding?.root?.findViewById(R.id.textView6)
        connectBtnTextView = binding?.root?.findViewById(R.id.imageView4)
        timerTextView = binding?.root?.findViewById(R.id.timeline)


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistner()
        backexitclick()
        requestVpnPermission()
        val countryName = arguments?.getString("countryName")
        val flagUrl = arguments?.getString("flagUrl")

        // Set data in the object
        Utility.countryName = countryName
        Utility.flagUrl = flagUrl
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
            val drawerLayout =
                requireActivity().findViewById<DrawerLayout>(R.id.constraintlayoutmenu)
            drawerLayout.openDrawer(GravityCompat.START)
        }
        binding?.imageView4?.setOnClickListener {
            if (hasVpnPermission()) {
                isButtonClicked = false
                btnConnectDisconnect()
                loadLottieAnimation()
                if (selectedCountry != null) {
                    binding?.power?.visibility = View.GONE
                    binding?.lottieAnimationView?.visibility = View.VISIBLE
                    binding?.connect?.text = "Connecting"
                    startVpnnew()
                    Handler().postDelayed({
                        val action = HomeFragmentDirections.actionHomeFragmentToRateScreenFragment()
                        findNavController().navigate(action)
                        isNavigationInProgress = true
                        isButtonClicked = true
                        isNavigationInProgress = false
                    }, 3000)
                }
            } else {
                // Permission is not granted, request it
                requestVpnPermission()
            }
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
                    val action = HomeFragmentDirections.actionHomeFragmentToServerListFragment()
                    findNavController().navigate(action)
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


    private fun alertdialog(dialogInterface: DialogInterface) {
        val alertSheetDialog = dialogInterface as AlertDialog
        val alertdialog = alertSheetDialog.findViewById<View>(
            com.google.android.material.R.id.alertTitle
        )
            ?: return
        alertdialog.setBackgroundColor(Color.TRANSPARENT)
    }




    override fun onStart() {
        super.onStart()
//        LocalBroadcastManager.getInstance(requireContext())
//            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        selectedCountry = arguments?.getParcelable("c") as? Countries
        type = arguments?.getString("type").toString()

        if (selectedCountry != null) {
            updateUI("LOAD")
            if (!Utility.isOnline(requireContext())) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            } else {
                startVpnnew()
            }
            imgFlag?.let {
                Glide.with(this)
                    .load(selectedCountry?.getFlagUrl1())
                    .into(it)
            }
            flagName?.text = selectedCountry?.getCountry1()
        }

        if (TextUtils.isEmpty(type)) {
            Log.v("AD_TYPE", "null")
        }
    }
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                intent.getStringExtra("state")?.let { updateUI(it) }
                Log.v("CHECKSTATE", intent.getStringExtra("state")!!)
                if (isFirst) {
                    if (getContext()?.let { RateScreenFragment.activeServer.getSavedServer(it)?.getCountry1() } != null) {
                        selectedCountry = getContext()?.let { RateScreenFragment.activeServer.getSavedServer(it) }
                        getContext()?.let {
                            imgFlag?.let { it1 ->
                                Glide.with(it)
                                    .load(selectedCountry?.getFlagUrl1())
                                    .into(it1)
                            }
                        }
                        flagName?.setText(selectedCountry?.getCountry1())
                    }
                    isFirst = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                var duration = intent.getStringExtra("duration")
                var lastPacketReceive = intent.getStringExtra("lastPacketReceive")
                var byteIn = intent.getStringExtra("byteIn")
                var byteOut = intent.getStringExtra("byteOut")
                if (duration == null) duration = "00:00:00"
                if (lastPacketReceive == null) lastPacketReceive = "0"
                if (byteIn == null) byteIn = " "
                if (byteOut == null) byteOut = " "
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
     fun updateConnectionStatus(
        duration: String?,
        lastPacketReceive: String?,
        byteIn: String,
        byteOut: String
    ) {
        val byteinKb = byteIn.split("-").toTypedArray()[1]
        val byteoutKb = byteOut.split("-").toTypedArray()[1]

        Utility.textDownloading!!.text = byteinKb
        Utility.textUploading!!.text = byteoutKb
        Utility.timerTextView!!.text = duration
    }
     private fun prepareVpn() {
        imgFlag?.let {
            Glide.with(this)
                .load(selectedCountry?.getFlagUrl1())
                .into(it)
        }
        flagName?.setText(selectedCountry?.getCountry1())
        if (Utility.isOnline(requireContext())) {
            if (selectedCountry != null) {
                val intent = VpnService.prepare(requireContext())
                Log.v("CHECKSTATE", "start")
                if (intent != null) {
                    startActivityForResult(intent, 1)
                } else startVpnnew()
            } else {
                isButtonClicked =  false
                Toast.makeText(context, "Please select a server first", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No Internet Connection, error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVpnnew() {
        try {
            // Log the value of selectedCountry
            Log.d("StartVPN", "selectedCountry: $selectedCountry")
            selectedCountry?.let { activeServer.saveServer(it, requireContext()) }
            OpenVpnApi.startVpn(
                requireContext(),
                selectedCountry?.getOvpn1(),
                selectedCountry?.getCountry1(),
                selectedCountry?.getOvpnUserName1(),
                selectedCountry?.getOvpnUserPassword1()
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun hasVpnPermission(): Boolean {
        val intent = VpnService.prepare(requireContext())
        return intent == null
    }

    private fun requestVpnPermission() {
        val intent = VpnService.prepare(requireContext())
        if (intent != null) {
            startActivityForResult(intent, VPN_PERMISSION_REQUEST_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                isButtonClicked = false
                if (selectedCountry != null) {
                    binding?.power?.visibility = View.GONE
                    binding?.lottieAnimationView?.visibility = View.VISIBLE
                    binding?.connect?.text = "Connecting"
                  lifecycleScope.launch {
                      isNavigationInProgress = true
                      isButtonClicked = true
                      isNavigationInProgress = false
                      startVpnnew()
                       delay(3000)
                       val action = HomeFragmentDirections.actionHomeFragmentToRateScreenFragment()
                       findNavController().navigate(action)
                   }
                }
            } else {
                // Permission denied
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkSelectedCountry() {

        if (selectedCountry == null) {
            isButtonClicked =  false
            updateUI("DISCONNECT")
            Toast.makeText(context, "Please select a server first", Toast.LENGTH_SHORT).show()
        } else {
            isButtonClicked =  true
            prepareVpn()
            updateUI("LOAD")
        }
    }

    private fun disconnectAlert() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Do you want to disconnect?")
        builder.setPositiveButton(
            "Disconnect"
        ) { _, _ ->
            disconnectFromVpn()
            STATUS = "DISCONNECTED"
            if (textDownloading != null) {
                textDownloading?.text = "0.0 kB/s"
            } else {
                Log.e("disconnectAlert", "textDownloading is null")
            }
            if (textUploading != null) {
                textUploading?.text = "0.0 kB/s"
            } else {
                Log.e("disconnectAlert", "textUploading is null")
            }

            Toast.makeText(context, "Server Disconnected", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ ->
            Toast.makeText(context, "Vpn Remains Connected", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun disconnectFromVpn() {
        try {
            OpenVPNThread.stop()
            updateUI("DISCONNECTED")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun btnConnectDisconnect() {
        if (STATUS == "DISCONNECTED") {
            if (Utility.isOnline(requireContext())) {
                checkSelectedCountry()
            } else {
                isButtonClicked = false
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Good...", Toast.LENGTH_SHORT).show()
        }
    }


}