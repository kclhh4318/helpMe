package com.example.helpme

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.app.ActivityCompat
import android.view.View
import com.google.android.material.bottomnavigation.LabelVisibilityMode

class MainActivity : AppCompatActivity() {

    var backPressedTime: Long = 0

    private lateinit var navController: NavController
    private lateinit var userNickname: String
    private lateinit var userEmail: String
    private lateinit var userProfileImage: String
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.nav_view)
        navView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        userNickname = intent.getStringExtra("nickname") ?: "No Nickname"
        userEmail = intent.getStringExtra("email") ?: "No Email"
        userProfileImage = intent.getStringExtra("profile_image") ?: ""

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_my_learning -> {
                    navController.navigate(R.id.navigation_my_learning)
                    updateBottomNavigationIcons(R.id.navigation_my_learning)
                    true
                }
                R.id.navigation_explore -> {
                    navController.navigate(R.id.navigation_explore)
                    updateBottomNavigationIcons(R.id.navigation_explore)
                    true
                }
                else -> false
            }
        }

        // 초기 선택 상태 설정
        updateBottomNavigationIcons(R.id.navigation_my_learning)
    }

    private fun updateBottomNavigationIcons(selectedItemId: Int) {
        val menu = navView.menu

        val homeItem = menu.findItem(R.id.navigation_my_learning)
        val exploreItem = menu.findItem(R.id.navigation_explore)

        homeItem.icon = if (selectedItemId == R.id.navigation_my_learning) {
            getDrawable(R.drawable.ic_home_on)
        } else {
            getDrawable(R.drawable.ic_home_off)
        }

        exploreItem.icon = if (selectedItemId == R.id.navigation_explore) {
            getDrawable(R.drawable.ic_book_on)
        } else {
            getDrawable(R.drawable.ic_book_off)
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime >= 1500) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 눌러서 종료하세요.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this)
            System.runFinalization()
            System.exit(0)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun showBottomNavigation() {
        navView.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        navView.visibility = View.GONE
    }
}
