package com.example.challapp.ui.feed.challange

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentChallangeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallangeFragment : Fragment() {
    private lateinit var binding:FragmentChallangeBinding
    private val challangeViewModel: ChallangeViewModel by viewModels()
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val previousDestination = findNavController().previousBackStackEntry?.destination
            if (previousDestination?.id != R.id.loginFragment && previousDestination?.id != R.id.splashScreenFragment) {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChallangeBinding.inflate(inflater,container,false)


        onClickNavigateDailyChallange()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        return binding.root
    }

    private fun onClickNavigateDailyChallange(){
        binding.buttonSeedailychallange.setOnClickListener {
            findNavController().navigate(R.id.action_challangeFragment_to_dailyChallangeFragment)
        }
    }
}