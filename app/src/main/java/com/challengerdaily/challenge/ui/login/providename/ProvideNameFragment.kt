package com.challengerdaily.challenge.ui.login.providename

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.FragmentProvideNameBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ProvideNameFragment : Fragment() {
    private lateinit var binding: FragmentProvideNameBinding

    private val provideNameViewModel: ProvideNameViewModel by viewModels()

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProvideNameBinding.inflate(inflater,container,false)

        onClickSubmitButton()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    private fun onClickSubmitButton(){
        binding.buttonSubmit.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val usersName = binding.textinputFullnameText.text.toString()
                if(validationName(usersName)){
                    if(provideNameViewModel.addUserName(usersName) == true){
                        findNavController().navigate(R.id.action_provideNameFragment_to_challangeFragment)
                    }else{
                        Snackbar.make(binding.root, getString(R.string.submit_name_error) , Snackbar.LENGTH_SHORT).show()
                    }
                }
                else{
                    Snackbar.make(binding.root, getString(R.string.fill_name_error) , Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validationName(inputText: String?): Boolean{
        return !inputText.isNullOrBlank() && inputText.length > 2
    }
}