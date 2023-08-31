package com.example.challapp.ui.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentTermsOfServiceBinding


class TermsOfServiceFragment : Fragment() {

    private lateinit var binding: FragmentTermsOfServiceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsOfServiceBinding.inflate(inflater,container,false)
        onClickNavigateBack()
        return binding.root
    }

    private fun onClickNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_termsOfServiceFragment_to_registerFragment)
        }
    }


}