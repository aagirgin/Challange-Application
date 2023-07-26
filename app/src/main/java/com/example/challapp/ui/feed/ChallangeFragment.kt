package com.example.challapp.ui.feed

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentChallangeBinding
import com.example.challapp.services.AuthService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChallangeFragment : Fragment() {
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChallangeBinding.inflate(inflater,container,false)
        val db = Firebase.firestore


        val docRef = AuthService.getCurrentUser()?.let { db.collection("Users").document(it.uid) }
        if (docRef != null) {
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val uname = document.getString("username")
                        //binding.GreetingText.text = "Hello, ${uname}"
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        binding.buttonprs.setOnClickListener {
            println(AuthService.getCurrentUser())
            AuthService.signOut()
            findNavController().navigate(R.id.action_challangeFragment_to_loginFragment)
            println(AuthService.getCurrentUser())
        }

        return binding.root
    }

}