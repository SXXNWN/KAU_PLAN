//package com.example.kau_plan
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
//
//        // 초기 화면
//        replaceFragment(HomeFragment())
//
//        bottomNav.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_home -> replaceFragment(HomeFragment())
//                R.id.nav_status -> replaceFragment(StatusFragment())
//                R.id.nav_consumption -> replaceFragment(ConsumptionFragment())
//                R.id.nav_board -> replaceFragment(BoardFragment())
//                R.id.nav_shop -> replaceFragment(ShopFragment())
//                R.id.nav_my_page -> replaceFragment(MyPageFragment())
//            }
//            true
//        }
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.nav_host, fragment)
//            .commit()
//    }
//}

package com.example.kau_plan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.kau_plan.ui.theme.KAU_PLANTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KAU_PLANTheme {
                HangPlanApp()   // ← 우리가 앞으로 만들 전체 UI
            }
        }
    }
}