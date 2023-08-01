package com.example.challapp.ui.login.splashscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentSplashScreenBinding
import com.example.challapp.ui.login.register.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
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

            splashScreenViewModel.currUserState.collect{state ->
                if (state){
                    findNavController().navigate(R.id.action_splashScreenFragment_to_challangeFragment)
                }
                else{
                    findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
                }
            }
        }
    }

}