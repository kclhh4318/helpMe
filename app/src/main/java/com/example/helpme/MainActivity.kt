package com.example.helpme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var userNickname: String
    private lateinit var userEmail: String
    private lateinit var userProfileImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)

        userNickname = intent.getStringExtra("nickname") ?: "No Nickname"
        userEmail = intent.getStringExtra("email") ?: "No Email"
        userProfileImage = intent.getStringExtra("profile_image") ?: ""

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_my_learning -> {
                    navController.navigate(R.id.navigation_my_learning)
                    true
                }
                R.id.navigation_explore -> {
                    navController.navigate(R.id.navigation_explore)
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
