package com.xilli.stealthnet.ui

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentHomeBinding
import com.xilli.stealthnet.helper.ActiveServer
import com.xilli.stealthnet.helper.Countries
import com.xilli.stealthnet.helper.Utility
import com.xilli.stealthnet.helper.Utility.updateUI
import com.xilli.stealthnet.ui.RateScreenFragment.Companion.STATUS
import com.xilli.stealthnet.ui.viewmodels.VpnViewModel
import top.oneconnectapi.app.OpenVpnApi.startVpn
import top.oneconnectapi.app.core.OpenVPNThread

class HomeFragment : Fragment() {
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
    val countryName = Utility.countryName
    val flagUrl = Utility.flagUrl
    val connecttextvie = Utility.connectionStateTextView
    private val vpnThread = OpenVPNThread()
    companion object {
        var type = ""
        val activeServer = ActiveServer()
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
        binding?.root?.let { Utility.initialize(requireContext(), it) }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistner()
        backpressed()
        val countryName = arguments?.getString("countryName")
        val flagUrl = arguments?.getString("flagUrl")
        Utility.countryName = countryName
        Utility.flagUrl = flagUrl
    }

    private fun backpressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
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
        //connect button for vpn still not using correctly!!!
        binding?.imageView4?.setOnClickListener {
            val intent = VpnService.prepare(requireContext())
            if (intent != null) {
                startActivityForResult(intent, VPN_PERMISSION_REQUEST_CODE)
            } else {
                isButtonClicked = false

                btnConnectDisconnect()

                if (selectedCountry != null) {
                    loadLottieAnimation()
                    binding?.power?.visibility = View.GONE
                    binding?.lottieAnimationView?.visibility = View.VISIBLE

                    Handler().postDelayed({
                        val action = HomeFragmentDirections.actionHomeFragmentToRateScreenFragment()
                        findNavController().navigate(action)

                    }, 3000)
                    isNavigationInProgress = true
                    isButtonClicked = true
                    isNavigationInProgress = false

                }
            }
        }
        binding?.constraintLayout2?.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToServerListFragment())
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

    //get the data from click and prepare the vpn
    override fun onStart() {
        super.onStart()
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                it, IntentFilter("connectionState"))
        }
       selectedCountry = arguments?.getParcelable("c") as? Countries
        type = arguments?.getString("type").toString()

        if (selectedCountry != null) {
            updateUI("LOAD")
            if (!Utility.isOnline(requireContext())) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            } else {
               prepareVpn()
            }
            if (selectedCountry != null) {
                updateUI("CONNECTED")
                imgFlag?.let {
                    Glide.with(this)
                        .load(selectedCountry?.getFlagUrl1())
                        .into(it)
                }
                flagName?.text = selectedCountry?.getCountry1()
            }
        }

        if (TextUtils.isEmpty(type)) {
            Log.v("AD_TYPE", "null")
        }
    }
//simple broadcastReceiver
private var broadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            updateUI(intent.getStringExtra("state")!!)
            Log.v("CHECKSTATE", intent.getStringExtra("state")!!)
            if (isFirst) {
                if (activeServer.getSavedServer(requireContext())?.getCountry1() != null) {
                    selectedCountry = activeServer.getSavedServer(requireContext())
                    imgFlag?.let {
                        Glide.with(requireContext())
                            .load(selectedCountry?.getFlagUrl1())
                            .into(it)
                    }
                    flagName?.text = selectedCountry?.getCountry1()
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
//its check the user connection and the data
    private fun prepareVpn() {
        updateCurrentVipServerIcon(selectedCountry?.getFlagUrl1())
        flagName?.setText(selectedCountry?.getCountry1())
        if (Utility.isOnline(requireContext())) {
            if (selectedCountry != null) {
                val intent = VpnService.prepare(requireContext())
                Log.v("CHECKSTATE", "start")
                if (intent != null) {
                    startActivityForResult(intent, 1)
                } else startVpn()
            } else {
                Toast.makeText(context, "Please select a server first", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateCurrentVipServerIcon(serverIcon: String?) {
        imgFlag?.let {
            Glide.with(this)
                .load(serverIcon)
                .into(it)
        }
    }
    //it start the vpn service
    private fun startVpn() {
        try {
            selectedCountry?.let { activeServer.saveServer(it, requireContext()) }
            startVpn(
                requireContext(),
                selectedCountry?.getOvpn1(),
                selectedCountry?.getCountry1(),
                selectedCountry?.getOvpnUserName1(),
                selectedCountry?.getOvpnUserPassword1()
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
    // for connect and disconnect
    private fun btnConnectDisconnect() {
        if (STATUS != "DISCONNECTED") {
            disconnectAlert()
        } else {
            if (!Utility.isOnline(requireContext())) {
                Toast.makeText(context, "No INTERNET CONNECTION", Toast.LENGTH_SHORT).show()
            } else {
                checkSelectedCountry()
            }
        }
    }
    // checking the selected country
    private fun checkSelectedCountry() {
        if (selectedCountry == null) {
            updateUI("DISCONNECT")
            Toast.makeText(context, "Select a seerver", Toast.LENGTH_SHORT).show()
        } else {
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
            Toast.makeText(context, "Server Disconnected", Toast.LENGTH_SHORT).show()

        }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ ->
            Toast.makeText(context, "VPN Remains Connected" +
                    "success" , Toast.LENGTH_SHORT).show()

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
    private fun noconnectionD() {
        val alertDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_vpn_connection, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setView(alertDialogView)

        val cancelButton = alertDialogView.findViewById<TextView>(R.id.Ok)

        val alertDialog = alertDialogBuilder.create()
        cancelButton.setOnClickListener {

            alertDialog.dismiss()
        }

        val dialogWindow = alertDialog.window
        dialogWindow?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog.show()
    }
    //on start it ask the vpn permission when and item clicked from server

     @Deprecated("Deprecated in Java")
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            Toast.makeText(context, "Start Downloand", Toast.LENGTH_SHORT).show()
            if (resultCode != RESULT_OK) {
                Log.d("Update", "Update failed$resultCode")
            }
        }
        if (resultCode == RESULT_OK) {
            startVpn()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}