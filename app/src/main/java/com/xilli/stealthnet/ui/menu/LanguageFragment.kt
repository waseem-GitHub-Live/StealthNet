package com.xilli.stealthnet.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentLanguageBinding
import com.xilli.stealthnet.databinding.FragmentSettingBinding


class LanguageFragment : Fragment() {
    private var binding:FragmentLanguageBinding?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistener()

    }

    private fun clicklistener() {
        binding?.backfromLanguage?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}