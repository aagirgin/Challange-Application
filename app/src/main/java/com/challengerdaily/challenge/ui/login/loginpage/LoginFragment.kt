package com.challengerdaily.challenge.ui.login.loginpage

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: FragmentLoginBinding

    private lateinit var googleSignInClient: GoogleSignInClient



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

        binding = FragmentLoginBinding.inflate(inflater,container,false)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this

        setupGoogleSignIn()
        setupLoginButton()
        onPressedNavigateRegisterPage(binding)
        onPressedNavigateForgotPassword(binding)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        return binding.root
    }
    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }
    private fun setupLoginButton() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.inputtextEmail.editText?.text.toString()
            val password = binding.inputtextPassword.editText?.text.toString()

            val isValid = validationFields(email, password)

            if (isValid) {
                loginViewModel.signIn(email, password)
            } else {
                Snackbar.make(binding.root, getString(R.string.fill_forms_error), Snackbar.LENGTH_SHORT).show()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                loginViewModel.loginState.collect { state ->
                    when (state) {
                        is LoginState.Error -> {
                            Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                        }
                        is LoginState.Success -> {
                            loginViewModel.currentUser.collect{user->
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
    }

    private fun setupGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

        binding.cardviewSignInGoogle.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                val intent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(intent)
            }
        }
    }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val signInResult = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (signInResult?.isSuccess == true) {
                val account = signInResult.signInAccount
                val idToken = account?.idToken
                if (idToken != null) {
                    lifecycleScope.launch {
                        account.email?.let { email -> loginViewModel.signInWithGoogle(idToken,email) }
                        viewLifecycleOwner.lifecycleScope.launch {
                            loginViewModel.loginGoogleState.collect{state->
                                when(state){
                                    is LoginState.Success -> {
                                        if(loginViewModel.firstTimeGoogleLogin.value){
                                            findNavController().navigate(R.id.action_loginFragment_to_provideNameFragment)
                                        }
                                        else{
                                            findNavController().navigate(R.id.action_loginFragment_to_challangeFragment)
                                        }

                                    }
                                    is LoginState.Error -> Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            } else {
                val errorMessage = signInResult?.status?.statusMessage ?: "Google Sign-In failed"
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
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