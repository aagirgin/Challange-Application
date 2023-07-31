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
import com.example.challapp.domain.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
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
        binding.buttonBacktosignup.setOnClickListener {
            findNavController().navigate(R.id.action_forgotpasswordFragment_to_loginFragment)
        }
    }


    private fun setupForgotButton(binding: FragmentForgotpasswordBinding) {
        binding.buttonRecover.setOnClickListener {
            val email = binding.inputtextForgotemail.editText?.text.toString()
            if (eMailFieldValidation(email)) {
                forgotpasswordViewModel.sendPasswordResetEmail(email)

                lifecycleScope.launch {
                    forgotpasswordViewModel.forgotPwState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                //TODO
                            }
                            is UiState.Error -> {
                                Toast.makeText(requireContext(), getString(R.string.email_Sent_unsuccesful), Toast.LENGTH_SHORT).show()
                            }
                            is UiState.Success -> {
                                Toast.makeText(requireContext(), getString(R.string.email_Sent_succesful), Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.fill_forms_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eMailFieldValidation(email: String?): Boolean {
        return !email.isNullOrBlank()
    }
}