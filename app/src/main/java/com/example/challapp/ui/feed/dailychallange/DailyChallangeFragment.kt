package com.example.challapp.ui.feed.dailychallange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.challapp.R
import com.example.challapp.databinding.FragmentDailyChallangeBinding

class DailyChallangeFragment : Fragment() {
    private lateinit var binding : FragmentDailyChallangeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyChallangeBinding.inflate(inflater, container, false)


        return binding.root
    }


}