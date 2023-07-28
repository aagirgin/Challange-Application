package com.example.challapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.challapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setBottomNavigationBarDestination()
        hideBottomNavigationBar()
    }

    private fun hideBottomNavigationBar() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashScreenFragment ||
                destination.id == R.id.registerFragment ||
                destination.id == R.id.loginFragment ||
                destination.id == R.id.forgotpasswordFragment ||
                destination.id == R.id.mailVerificationFragment
            ) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
    private fun setBottomNavigationBarDestination(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navhomeFragment -> {
                    navController.navigate(R.id.challangeFragment)
                    true
                }
                R.id.navcommunityFragment -> {
                    navController.navigate(R.id.groupFragment)
                    true
                }
                R.id.navprofileFragment -> {
                    navController.navigate(R.id.profileNavFragment)
                    true
                }
                else -> false
            }
        }
    }
}
