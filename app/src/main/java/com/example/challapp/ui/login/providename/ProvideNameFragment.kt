package com.example.challapp.ui.login.providename

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentProvideNameBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ProvideNameFragment : Fragment() {
    private lateinit var binding: FragmentProvideNameBinding

    private val provideNameViewModel: ProvideNameViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProvideNameBinding.inflate(inflater,container,false)

        onClickSubmitButton()

        return binding.root
    }

    private fun onClickSubmitButton(){
        binding.buttonSubmit.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val usersName = binding.textinputFullnameText.text.toString()
                if(provideNameViewModel.addUserName(usersName) == true){
                    findNavController().navigate(R.id.action_provideNameFragment_to_challangeFragment)
                }else{
                    Snackbar.make(binding.root, getString(R.string.submit_name_error) , Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

}