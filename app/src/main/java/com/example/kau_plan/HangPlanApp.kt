package com.example.kau_plan

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.kau_plan.ui.theme.navigation.AppNavigation

@Composable
fun HangPlanApp() {
    AppNavigation()   // 네비게이션 전체를 실행
}

@Preview(showBackground = true)
@Composable
fun HangPlanAppPreview() {
    HangPlanApp()
}