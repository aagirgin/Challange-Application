package com.example.challapp.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentSplashScreenBinding
import com.example.challapp.services.AuthService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSplashScreenBinding.inflate(inflater,container,false)

        checkCurrentUserAndNavigate()

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun checkCurrentUserAndNavigate() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            val user = AuthService.getCurrentUser()
            if (user == null) {
                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
            } else {
                findNavController().navigate(R.id.action_splashScreenFragment_to_challangeFragment)
            }
        }
    }

}