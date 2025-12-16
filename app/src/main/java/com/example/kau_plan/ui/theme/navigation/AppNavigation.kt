package com.example.kau_plan.ui.theme.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kau_plan.data.Expense
import com.example.kau_plan.ui.theme.expense.AddExpenseScreen
import com.example.kau_plan.ui.theme.expense.ExpenseListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kau_plan.ui.theme.board.BoardScreen
import com.example.kau_plan.ui.theme.board.PostDetailScreen
import com.example.kau_plan.ui.theme.expense.ExpenseViewModel
import com.example.kau_plan.ui.theme.home.HomeScreen
import com.example.kau_plan.ui.theme.profile.ProfileScreen
import com.example.kau_plan.ui.theme.board.WritePostScreen

// 각 화면의 정보를 담는 Sealed Class 추가
//sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
//    object Home : Screen("home", "홈", Icons.Default.Home)
//    object UsageStatus : Screen("usage_status", "이용현황", Icons.Default.Search) // 예시 경로
//    object Expense : Screen("expense", "소비내역", Icons.Default.Lightbulb)
//    object Board : Screen("board", "게시판", Icons.Default.BarChart)
//    object Profile : Screen("profile", "프로필", Icons.Default.Person)
//}

@Composable
fun AppNavigation(
    navController: androidx.navigation.NavHostController,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {

    val viewModel: ExpenseViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "expense_list",
        modifier = modifier
    ) {

        // 🔹 소비내역 화면
        composable("expense_list") {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
        }

        composable("add_expense") {
            AddExpenseScreen(
                onSaveClick = { expense: Expense ->
                    viewModel.addExpense(expense)   // ★ 실제 저장
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = "expense") {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
        }

        // 🔹 홈 화면 (기존 'home' 라우트를 새로운 HomeScreen으로 연결)
        composable(route = "home") {
            HomeScreen(
                navController = navController
                // 필요하다면 ViewModel의 데이터를 HomeScreen에 전달할 수 있습니다.
                // 예: monthlyTotal = viewModel.monthlyTotal,
                // 예: onNavigateToBoard = { navController.navigate("board") }
            )
        }

        // 🔹 게시판 화면 (새로 추가)
        composable(route = "board") {
            BoardScreen(
                navController = navController
            )
        }

        composable("postDetail") {
            PostDetailScreen(navController = navController)
        }

        composable("writePost") {
            WritePostScreen(navController = navController)
        }

        // 🔹 프로필 화면
        composable(route = "profile") {
            ProfileScreen(
                monthlyTotal = viewModel.monthlyTotal,
                expenses = viewModel.expenses
            )
        }
    }
}