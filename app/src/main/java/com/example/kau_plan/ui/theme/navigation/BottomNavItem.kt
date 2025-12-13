package com.example.kau_plan.ui.theme.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem("home", "홈", Icons.Outlined.Home)
    data object Status : BottomNavItem("status", "이용현황", Icons.Outlined.Search)
    data object Expense : BottomNavItem("expense_list", "소비내역", Icons.Outlined.ReceiptLong)
    data object Board : BottomNavItem("board", "게시판", Icons.Outlined.Forum)
    data object Shop : BottomNavItem("shop", "쇼핑", Icons.Outlined.ShoppingBag)
    data object Profile : BottomNavItem("profile", "프로필", Icons.Outlined.Person)
}