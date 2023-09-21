package com.xilli.stealthnet.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xilli.stealthnet.R
import com.xilli.stealthnet.adapter.SearchView_Free_Adapter
import com.xilli.stealthnet.adapter.SearchView_Premium_Adapter
import com.xilli.stealthnet.data.DataItemFree
import com.xilli.stealthnet.data.DataItemPremium
import com.xilli.stealthnet.databinding.FragmentServerListBinding
import com.xilli.stealthnet.helper.Config
import com.xilli.stealthnet.helper.Constants
import com.xilli.stealthnet.helper.Countries
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ServerListFragment : Fragment() {
    private var binding: FragmentServerListBinding?=null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPREMIUM: SearchView_Premium_Adapter
    private lateinit var adapterFREE: SearchView_Free_Adapter
    private var isBackgroundChanged = false
    private var selectedPosition = RecyclerView.NO_POSITION
    private  var selectedServer: DataItemPremium?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentServerListBinding.inflate(inflater, container, false)
        selectedServer = DataItemPremium("Default Server", "10.0.0.5", R.drawable.flag, R.drawable.ic_signal, R.drawable.ic_green_crown)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPremiumRecyclerView()
        setupFreeRecyclerView()
        clicklistner()
        loadServers()
        binding?.constraintLayout2?.performClick()
    }
    private fun setupPremiumRecyclerView() {
        recyclerView = binding?.recyclerView ?: return

        val premiumServers = loadServersvip()

        adapterPREMIUM = SearchView_Premium_Adapter(requireContext(), premiumServers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterPREMIUM

        adapterPREMIUM.setOnItemClickListener { position ->
            adapterPREMIUM.setSelectedPosition(position)
            adapterFREE.resetSelection()

            adapterPREMIUM.notifyDataSetChanged()
            adapterFREE.notifyDataSetChanged()
        }
    }


    private fun setupFreeRecyclerView() {
        recyclerView = binding?.recyclerview2 ?: return
        adapterFREE = SearchView_Free_Adapter(requireContext(), freeServers,selectedServer)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterFREE

        adapterFREE.setOnItemClickListener { position ->
            adapterFREE.setSelectedPosition(position)
            adapterPREMIUM.resetSelection()

            adapterFREE.notifyDataSetChanged()
            adapterPREMIUM.notifyDataSetChanged()
        }
    }
    private fun clicklistner() {
        binding?.imageView7?.setOnClickListener {
            findNavController().popBackStack()
        }
        binding?.constraintLayout2?.setOnClickListener {
            val filteredServers: List<Countries?> = if (isBackgroundChanged) {
                vipServers.map { it.toCountries() }
            } else {
                selectedServer?.let { listOf(it.toCountries()) } ?: emptyList()
            }

            adapterPREMIUM.setData(filteredServers)

            selectedPosition = RecyclerView.NO_POSITION
            adapterFREE.resetSelection()
            adapterPREMIUM.resetSelection()
            adapterFREE.notifyDataSetChanged()
            adapterPREMIUM.notifyDataSetChanged()
            isBackgroundChanged = !isBackgroundChanged
            updateBackgroundState()
            binding?.radio?.isChecked = !binding?.radio?.isChecked!!
        }

    }
    fun DataItemPremium.toCountries(): Countries {
        return Countries(this.title, this.flagimageUrl.toString(), this.IPdescription)
    }


    private fun updateBackgroundState() {
        if (isBackgroundChanged) {
            binding?.constraintLayout2?.setBackgroundResource(R.drawable.selector_background)
        } else {
            binding?.constraintLayout2?.setBackgroundResource(R.drawable.background_black_card)
        }
    }
    private fun loadServers() {
        Log.d("MyApp", "loadServers() method started") // Log that the method has started
        val servers: java.util.ArrayList<Countries?> = java.util.ArrayList<Countries?>()
        try {
            val jsonArray: JSONArray = JSONArray(Constants.FREE_SERVERS)
            if (jsonArray.length() == 1 && jsonArray[0] is JSONObject) {
                val jsonObject = jsonArray.getJSONObject(0)
                if (jsonObject.has("result") && jsonObject.getString("result") == "error") {
                    val errorMessage = jsonObject.getString("message")
                    Log.e("MyApp", "API Error: $errorMessage")
                    return
                }
            }
            for (i in 0 until jsonArray.length()) {
                Log.d(
                    "MyApp",
                    "Processing JSON object #$i"
                ) // Log which JSON object is being processed
                val `object` = jsonArray[i] as JSONObject
                servers.add(
                    Countries(
                        `object`.getString("serverName"),
                        `object`.getString("flag_url"),
                        `object`.getString("ovpnConfiguration"),
                        `object`.getString("vpnUserName"),
                        `object`.getString("vpnPassword")

                    )
                )
                if (i % 2 == 0 && i > 0) {
                    if (!Config.vip_subscription && !Config.all_subscription) {
                        servers.add(null)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("MyApp", "JSON parsing error: " + e.message) // Log any JSON parsing errors
        }
        adapterFREE.setData(servers)
        Log.d("MyApp", "loadServers() method completed") // Log that the method has completed
    }

    private fun loadServersvip(): List<Countries> {
        val servers = ArrayList<Countries>()
        try {
            val jsonArray = JSONArray(Constants.PREMIUM_SERVERS)
            for (i in 0 until jsonArray.length()) {
                val `object` = jsonArray[i] as JSONObject
                servers.add(
                    Countries(
                        `object`.getString("serverName"),
                        `object`.getString("flag_url"),
                        `object`.getString("ovpnConfiguration"),
                        `object`.getString("vpnUserName"),
                        `object`.getString("vpnPassword")
                    )
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return servers
    }

    private val vipServers = listOf(
        DataItemPremium("VIP Server 1", "10.0.0.1", R.drawable.flag, R.drawable.ic_signal, R.drawable.ic_green_crown),
        DataItemPremium("VIP Server 2", "10.0.0.2", R.drawable.flag, R.drawable.ic_signal, R.drawable.ic_green_crown)
    )

    private val freeServers = listOf(
        DataItemFree("Free Server 1", "10.0.0.3", R.drawable.flag, R.drawable.ic_signal),
        DataItemFree("Free Server 2", "10.0.0.4", R.drawable.flag, R.drawable.ic_signal)
    )

    private val defaultServer = DataItemFree("Default Server", "10.0.0.5", R.drawable.flag, R.drawable.ic_signal)

}