package com.example.kau_plan.ui.theme.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Icon

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Status,
        BottomNavItem.Expense,
        BottomNavItem.Board,
        BottomNavItem.Shop,
        BottomNavItem.Profile
    )

    val currentRoute = navController.currentBackStackEntryAsState().value
        ?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // 같은 탭 여러번 눌러도 스택이 쌓이지 않게 하는 패턴
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF6B4DFF),
                    selectedTextColor = Color(0xFF6B4DFF),
                    indicatorColor = Color(0xFFEDE7FF),
                    unselectedIconColor = Color(0xFF777777),
                    unselectedTextColor = Color(0xFF777777)
                )
            )
        }
    }
}