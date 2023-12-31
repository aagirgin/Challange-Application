package com.challengerdaily.challenge.ui.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.FragmentMailVerificationBinding


class MailVerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMailVerificationBinding.inflate(inflater,container,false)

        onPressedBackLoginPage(binding)

        return binding.root
    }

    private fun onPressedBackLoginPage(binding: FragmentMailVerificationBinding) {
        binding.buttonBacktosignup.setOnClickListener {
            findNavController().navigate(R.id.action_mailVerificationFragment_to_loginFragment)
        }
    }



}