package com.example.challapp.ui.login.register

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
import com.example.challapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
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
        binding.backLoginTextButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun EmailVerification() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                findNavController().navigate(R.id.action_registerFragment_to_mailVerificationFragment)
            }
        }
    }

    private fun setupSignUpButton(binding: FragmentRegisterBinding) {
        binding.signupButton.setOnClickListener {
            binding.signupButton.isEnabled = false

            val email = binding.emailField.editText?.text.toString()
            val password = binding.passwordField.editText?.text.toString()
            val fullName = binding.fullNameField.editText?.text.toString()

            val isValid = validationFields(email,password,fullName)

            if (isValid){
                registerViewModel.signUp(email, password, fullName)

                lifecycleScope.launch {
                    registerViewModel.registerState.collect { state ->
                        when (state) {
                            is RegisterState.Loading -> {
                               //TODO
                            }
                            is RegisterState.Success -> {
                                EmailVerification()
                            }
                            is RegisterState.Error -> {
                                Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                                binding.signupButton.isEnabled = true
                            }
                            else -> {}
                        }
                    }
                }
            }
            else{
                Toast.makeText(requireContext(), "Please fill all forms.", Toast.LENGTH_SHORT).show()
            }
            binding.signupButton.isEnabled = true
        }
    }



    private fun validationFields(email:String?,password:String?,fullName:String?): Boolean{

        if (!email.isNullOrBlank() && !password.isNullOrBlank() && !fullName.isNullOrBlank()) {
            return true
        }
        return false
    }
}