package com.example.challapp.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentLoginBinding
import com.example.challapp.services.AuthService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater,container,false)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this

        setupLoginButton(binding)
        onPressedNavigateRegisterPage(binding)
        onPressedNavigateForgotPassword(binding)

        return binding.root
    }

    private fun setupLoginButton(binding: FragmentLoginBinding) {
        binding.loginButton.setOnClickListener {
            val email = binding.emailField.editText?.text.toString()
            val password = binding.passwordField.editText?.text.toString()

            val isValid = validationFields(email, password)

            if (isValid) {
                loginViewModel.signIn(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill all forms.", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // TODO: Show loading indicator or progress bar
                    }

                    is LoginState.Error -> {
                        Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                    }

                    is LoginState.Success -> {
                        val user = AuthService.getCurrentUser()
                        if (user != null && user.isEmailVerified) {
                            findNavController().navigate(R.id.action_loginFragment_to_challangeFragment)
                        } else {
                            Toast.makeText(requireContext(), "Please verify your Mail.", Toast.LENGTH_SHORT).show()
                        }
                        loginViewModel.resetLoginState()
                    }
                    else -> {}
                }
            }
        }
    }
    private fun validationFields(email:String?,password:String?): Boolean{

        if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
            return true
        }
        return false
    }
    private fun onPressedNavigateRegisterPage(binding: FragmentLoginBinding){
        binding.signUpTextButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun onPressedNavigateForgotPassword(binding: FragmentLoginBinding){
        binding.forgotpasswordTextButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotpasswordFragment)
        }
    }
}