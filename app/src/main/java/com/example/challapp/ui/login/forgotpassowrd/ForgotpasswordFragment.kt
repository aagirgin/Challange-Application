package com.example.challapp.ui.login.forgotpassowrd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentForgotpasswordBinding
import kotlinx.coroutines.launch


class ForgotpasswordFragment : Fragment() {
    private val forgotpasswordViewModel: ForgotPasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentForgotpasswordBinding.inflate(inflater,container,false)

        setupForgotButton(binding)
        onPressedBackLoginPage(binding)

        return binding.root
    }


    private fun onPressedBackLoginPage(binding: FragmentForgotpasswordBinding) {
        binding.backtologinButton.setOnClickListener {
            findNavController().navigate(R.id.action_forgotpasswordFragment_to_loginFragment)
        }
    }


    private fun setupForgotButton(binding: FragmentForgotpasswordBinding) {
        binding.forgotButton.setOnClickListener {
            val email = binding.forgotemailField.editText?.text.toString()
            if (eMailFieldValidation(email)) {
                forgotpasswordViewModel.sendPasswordResetEmail(email)

                lifecycleScope.launch {
                    forgotpasswordViewModel.forgotPwState.collect { state ->
                        when (state) {
                            is ForgotPwState.Loading -> {
                                //TODO
                            }
                            is ForgotPwState.Error -> {
                                Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                            }
                            is ForgotPwState.Success -> {
                                Toast.makeText(requireContext(), "Email successfully sent to reset your password.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill Email field.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eMailFieldValidation(email: String?): Boolean {
        return !email.isNullOrBlank()
    }
}