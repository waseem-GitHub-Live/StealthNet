package com.xilli.stealthnet.ui

import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xilli.stealthnet.R
import com.xilli.stealthnet.adapter.SearchView_Free_Adapter
import com.xilli.stealthnet.adapter.SearchView_Premium_Adapter
import com.xilli.stealthnet.data.DataItemFree
import com.xilli.stealthnet.data.DataItemPremium
import com.xilli.stealthnet.adapter.OnItemSelectedListener
import com.xilli.stealthnet.databinding.FragmentServerListBinding
import java.util.ArrayList

class ServerListFragment : Fragment() {
    private var binding: FragmentServerListBinding?=null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPREMIUM: SearchView_Premium_Adapter
    private lateinit var adapterFREE: SearchView_Free_Adapter
    private var mList1 = ArrayList<DataItemPremium>()
    private var mList2 = ArrayList<DataItemFree>()
    private var isBackgroundChanged = false
    private var selectedPosition = RecyclerView.NO_POSITION
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentServerListBinding.inflate(inflater, container, false)
        addDataToList()
        addDataToList2()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPremiumRecyclerView()
        setupFreeRecyclerView()
        clicklistner()
    }

    private fun setupPremiumRecyclerView() {
        recyclerView = binding?.recyclerView ?: return
        adapterPREMIUM = SearchView_Premium_Adapter(requireContext(), mList1)
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
        adapterFREE = SearchView_Free_Adapter(requireContext(), mList2)
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
            isBackgroundChanged = !isBackgroundChanged
            updateBackgroundState()
            binding?.radio?.isChecked = !binding?.radio?.isChecked!!

            selectedPosition = RecyclerView.NO_POSITION
            adapterFREE.setSelectedPosition(selectedPosition)
            adapterPREMIUM.setSelectedPosition(selectedPosition)

            adapterFREE.notifyDataSetChanged()
            adapterPREMIUM.notifyDataSetChanged()

        }
    }


    private fun updateBackgroundState() {
        if (isBackgroundChanged) {
            binding?.constraintLayout2?.setBackgroundResource(R.drawable.selector_background)
        } else {
            binding?.constraintLayout2?.setBackgroundResource(R.drawable.background_black_card)
        }
    }


    private fun addDataToList() {
        mList1.add(DataItemPremium("Neatherland", "22.22.22.22", R.drawable.flag, R.drawable.ic_signal, R.drawable.ic_green_crown))
        mList1.add(DataItemPremium("SriLanka", "22.22.22.22", R.drawable.flag, R.drawable.ic_signal, R.drawable.ic_green_crown))
    }
    private fun addDataToList2() {
        mList2.add(DataItemFree("Neatherland", "22.22.22.22", R.drawable.flag, R.drawable.ic_signal))
        mList2.add(DataItemFree("SriLanka", "22.22.22.22", R.drawable.flag, R.drawable.ic_signal))
    }
}