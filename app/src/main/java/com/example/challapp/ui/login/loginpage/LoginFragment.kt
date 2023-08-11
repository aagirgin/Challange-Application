package com.example.challapp.ui.login.loginpage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val previousDestination = findNavController().previousBackStackEntry?.destination
            if (previousDestination?.id == R.id.profileNavFragment) {
                requireActivity().finish()
             }else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

    }
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        return binding.root
    }
    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }
    private fun setupLoginButton(binding: FragmentLoginBinding) {
        binding.buttonLogin.setOnClickListener {
            val email = binding.inputtextEmail.editText?.text.toString()
            val password = binding.inputtextPassword.editText?.text.toString()

            val isValid = validationFields(email, password)

            if (isValid) {
                loginViewModel.signIn(email, password)
            } else {
                Snackbar.make(binding.root, getString(R.string.fill_forms_error), Snackbar.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Error -> {
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                    is LoginState.Success -> {
                        loginViewModel.currentUser.collect{user->
                            println(user)
                            if (user != null) {
                                loginViewModel.updateStreakOnNavigate()
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