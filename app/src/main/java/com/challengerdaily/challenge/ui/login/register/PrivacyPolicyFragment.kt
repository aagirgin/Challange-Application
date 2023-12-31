package com.challengerdaily.challenge.ui.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment() {
    private lateinit var binding: FragmentPrivacyPolicyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivacyPolicyBinding.inflate(inflater,container,false)
        onClickNavigateBack()
        return binding.root
    }

    private fun onClickNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_privacyPolicyFragment_to_registerFragment)
        }
    }

}