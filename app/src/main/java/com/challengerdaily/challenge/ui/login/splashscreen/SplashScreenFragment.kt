package com.challengerdaily.challenge.ui.login.splashscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.FragmentSplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSplashScreenBinding.inflate(inflater,container,false)

        checkCurrentUserAndNavigate()

        return binding.root
    }

    private fun checkCurrentUserAndNavigate() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)

            splashScreenViewModel.getCurrentUser.collect{ state ->
                if (state != null){
                    splashScreenViewModel.getName()
                    splashScreenViewModel.getCurrentUserName.collect{ userName ->
                        if(userName == null && splashScreenViewModel.getCurrentUser.value?.uid != null){
                            findNavController().navigate(R.id.action_splashScreenFragment_to_provideNameFragment)
                        }
                        else{
                            splashScreenViewModel.updateStreakOnNavigate()
                            findNavController().navigate(R.id.action_splashScreenFragment_to_challangeFragment)
                        }
                    }
                }
                else{
                    findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
                }
            }
        }
    }

}