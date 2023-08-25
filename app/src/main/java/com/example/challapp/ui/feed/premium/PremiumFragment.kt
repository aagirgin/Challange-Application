package com.example.challapp.ui.feed.premium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentPremiumBinding


class PremiumFragment : Fragment() {

    private lateinit var binding: FragmentPremiumBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPremiumBinding.inflate(inflater,container,false)
        onClickNavigateBack()
        return binding.root
    }

    private fun onClickNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_premiumFragment_to_challangeFragment)
        }
    }
}