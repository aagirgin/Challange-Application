package com.example.challapp.ui.login.loginpg

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
import com.example.challapp.repository.FirestoreUserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
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
        binding.buttonLogin.setOnClickListener {
            val email = binding.inputtextEmail.editText?.text.toString()
            val password = binding.inputtextPassword.editText?.text.toString()

            val isValid = validationFields(email, password)

            if (isValid) {
                loginViewModel.signIn(email, password)
            } else {
                Toast.makeText(requireContext(), getString(R.string.fill_forms_error), Toast.LENGTH_SHORT).show()
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
                        loginViewModel.currentUser.collect{user->
                            if (user != null && user.isEmailVerified) {
                                findNavController().navigate(R.id.action_loginFragment_to_challangeFragment)
                            }
                        }

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
        binding.buttonBacktosignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun onPressedNavigateForgotPassword(binding: FragmentLoginBinding){
        binding.buttonForgotpassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotpasswordFragment)
        }
    }
}