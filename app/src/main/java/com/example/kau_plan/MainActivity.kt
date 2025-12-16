package com.example.kau_plan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kau_plan.ui.theme.KAU_PLANTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 앱의 메인 화면을 설정하는 부분입니다.
        setContent {
            // 앱의 전체 테마를 적용합니다.
            KAU_PLANTheme {
                // 직접 만드신 HangPlanApp Composable을 호출하여 화면에 표시합니다.
                HangPlanApp()
            }
        }
    }
}