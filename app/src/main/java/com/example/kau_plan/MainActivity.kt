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