package com.xilli.stealthnet.ui.menu

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentMenuBinding
import com.xilli.stealthnet.ui.HomeFragmentDirections


class MenuFragment : Fragment() {
    private var binding:FragmentMenuBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        binding?.constraintlayoutmenu?.setBackgroundColor(0)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklictener()

    }

    private fun clicklictener() {
        binding?.settings?.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToSettingFragment()
            findNavController().navigate(action)
        }
        binding?.serverList?.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToServerListFragment()
            findNavController().navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}