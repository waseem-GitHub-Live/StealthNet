package com.xilli.stealthnet.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentRateScreenBinding
import com.xilli.stealthnet.databinding.FragmentReportScreenBinding


class ReportScreenFragment : Fragment() {
    private var binding:FragmentReportScreenBinding?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportScreenBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistener()
    }

    private fun clicklistener() {
        binding?.imageView5?.setOnClickListener {
            findNavController().popBackStack()
        }
        binding?.backHome?.setOnClickListener {
            val action = ReportScreenFragmentDirections.actionReportScreenFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        binding?.connectionagain?.setOnClickListener {
            val action = ReportScreenFragmentDirections.actionReportScreenFragmentToServerListFragment()
            findNavController().navigate(action)
        }
    }
}