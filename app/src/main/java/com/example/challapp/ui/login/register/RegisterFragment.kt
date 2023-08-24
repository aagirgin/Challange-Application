package com.example.challapp.ui.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val registerViewModel: RegisterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentRegisterBinding.inflate(inflater,container,false)


        onPressedBackLoginPage(binding)
        setupSignUpButton(binding)

        return binding.root
    }

    private fun onPressedBackLoginPage(binding: FragmentRegisterBinding) {
        binding.buttonBacktologin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }


    private fun setupSignUpButton(binding: FragmentRegisterBinding) {
        binding.buttonSignup.setOnClickListener {
            binding.buttonSignup.isEnabled = false

            val email = binding.textinputEmail.editText?.text.toString()
            val password = binding.textinputPassword.editText?.text.toString()
            val fullName = binding.textinputFullname.editText?.text.toString()

            val isValid = validationFields(email, password, fullName)

            if (isValid) {
                lifecycleScope.launch {
                    registerViewModel.signUp(email, password, fullName)
                    registerViewModel.registerState.collect{
                            state ->
                        when (state) {
                            is RegisterState.Success -> {
                                Snackbar.make(binding.root, getString(R.string.successful_register), Snackbar.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_registerFragment_to_mailVerificationFragment)
                                registerViewModel.resetState()
                            }
                            is RegisterState.Error -> {
                                Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                                registerViewModel.resetState()
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                Snackbar.make(binding.root, getString(R.string.fill_forms_error), Snackbar.LENGTH_SHORT).show()
            }
            binding.buttonSignup.isEnabled = true
        }
    }


    private fun validationFields(email:String?,password:String?,fullName:String?): Boolean{

        if (!email.isNullOrBlank() && !password.isNullOrBlank() && !fullName.isNullOrBlank()) {
            return true
        }
        return false
    }
}