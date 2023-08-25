package com.example.challapp.ui.feed.challange

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentChallangeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallangeFragment : Fragment() {
    private lateinit var binding:FragmentChallangeBinding
    private val challangeViewModel: ChallangeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChallangeBinding.inflate(inflater,container,false)
        binding.viewModel = challangeViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        println(challangeViewModel.getCurrentUser.value?.uid)

        showDataOnScreen()
        onClickNavigatePreviousChallenges()
        onClickNavigateDailyChallange()
        onClickNavigatePremium()
        onClickNavigateNotifications()

        return binding.root
    }

    private fun showDataOnScreen() {
        viewLifecycleOwner.lifecycleScope.launch {
            challangeViewModel.getUserStreakCount.collect { streakCount ->
                binding.textviewStreakCount.text = streakCount.toString()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            challangeViewModel.notificationCount.collect { notificationCount ->
                binding.textviewNotificationcount.text = notificationCount.toString()
            }
        }
    }
    private fun onClickNavigateDailyChallange(){
        binding.buttonSeedailychallange.setOnClickListener {
            findNavController().navigate(R.id.action_challangeFragment_to_dailyChallangeFragment)
        }
    }

    private fun onClickNavigatePremium(){
        binding.cardviewPremium.setOnClickListener {
            findNavController().navigate(R.id.action_challangeFragment_to_premiumFragment)
        }
    }
    private fun onClickNavigatePreviousChallenges(){
        binding.cardviewCompletedchallenges.setOnClickListener {
            findNavController().navigate(R.id.action_challangeFragment_to_completedChallengeFragment)
        }
    }
    private fun onClickNavigateNotifications(){
        binding.imageviewNoticication.setOnClickListener {
            findNavController().navigate(R.id.action_challangeFragment_to_notificationFragment)
        }
    }

}