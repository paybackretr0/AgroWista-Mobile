package com.example.agrowista

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            userPreferences = UserPreferences.getInstance(this)

            val navController = findNavController(R.id.nav_host_fragment_activity_main)

            val shouldNavigateLogin = intent.getBooleanExtra("should_navigate_login", false)
            if (shouldNavigateLogin) {
                navController.navigate(R.id.nav_login)
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.nav_login, R.id.nav_regis, R.id.nav_camera -> hideBottomNavigationAndTopAppBar()
                    else -> showBottomNavigationAndTopAppBar()
                }
            }

            binding.bottomNav.setupWithNavController(navController)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideBottomNavigationAndTopAppBar() {
        supportActionBar?.hide()
        binding.bottomNav.visibility = View.GONE
    }

    private fun showBottomNavigationAndTopAppBar() {
        supportActionBar?.show()
        binding.bottomNav.visibility = View.VISIBLE
    }
}