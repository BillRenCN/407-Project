package com.cs407.project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cs407.project.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_listing, R.id.navigation_profile, R.id.navigation_messages
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    fun replaceProfileFragmentWithEditProfile() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.editProfileFragment)
    }

    fun navigateEditPassword() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.editPasswordFragment)
    }

    fun navigateEditUsername() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.editUsernameFragment)
    }

}