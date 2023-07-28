package com.example.challapp.ui.feed.feedmain

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentChallangeBinding
import com.example.challapp.ui.login.forgotpassowrd.ForgotPasswordViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallangeFragment : Fragment() {
    private lateinit var binding:FragmentChallangeBinding
    private val challangeViewModel: ChallangeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChallangeBinding.inflate(inflater,container,false)

        binding.buttonprs.setOnClickListener {
            challangeViewModel.signOutFromSession()
            findNavController().navigate(R.id.action_challangeFragment_to_loginFragment)
        }

        return binding.root
    }

}