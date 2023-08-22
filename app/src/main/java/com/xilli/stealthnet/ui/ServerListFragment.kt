package com.xilli.stealthnet.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.xilli.stealthnet.R
import com.xilli.stealthnet.databinding.FragmentServerListBinding

class ServerListFragment : Fragment() {
    private var binding: FragmentServerListBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServerListBinding.inflate(inflater, container, false)
        Searchview()

        return binding?.root
    }

    private fun Searchview() {
        val searchView = binding?.SearchView

        val hintColor = ContextCompat.getColor(requireContext(), R.color.custom_hint_color) // Use requireContext() here
        searchView?.queryHint = "Search"
//        searchView.setQueryHintTextColor(hintColor)

        val textColor = ContextCompat.getColor(requireContext(), android.R.color.white) // White text color
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchView?.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)?.setTextColor(textColor)
                return true
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicklistner()
    }

    private fun clicklistner() {
        binding?.imageView7?.setOnClickListener {
            findNavController().popBackStack()
        }

    }
}