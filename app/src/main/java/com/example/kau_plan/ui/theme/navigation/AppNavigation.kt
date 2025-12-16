package com.example.kau_plan.ui.theme.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kau_plan.ReservationScreen // ReservationScreen import
import com.example.kau_plan.StatusScreen
import com.example.kau_plan.data.Expense
import com.example.kau_plan.ui.theme.expense.AddExpenseScreen
import com.example.kau_plan.ui.theme.expense.ExpenseListScreen
import com.example.kau_plan.ui.theme.expense.ExpenseViewModel
import com.example.kau_plan.ui.theme.profile.ProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: ExpenseViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        // 🔹 홈 화면
        composable(BottomNavItem.Home.route) {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
        }

        // 🔹 현황 화면
        composable(BottomNavItem.Status.route) {
            StatusScreen(
                onNavigateToReservation = {
                    navController.navigate("reservation_screen")
                }
            )
        }

        // 🔹 가계부 화면
        composable(BottomNavItem.Expense.route) {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
        }

        // 🔹 게시판 화면 (임시)
        composable(BottomNavItem.Board.route) {
            Text("게시판 화면")
        }

        // 🔹 상점 화면 (임시)
        composable(BottomNavItem.Shop.route) {
            Text("상점 화면")
        }

        // 🔹 프로필(내정보) 화면
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                monthlyTotal = viewModel.monthlyTotal,
                expenses = viewModel.expenses
            )
        }

        // 🔹 소비내역 '추가' 화면
        composable("add_expense") {
            AddExpenseScreen(
                onSaveClick = { expense: Expense ->
                    viewModel.addExpense(expense)
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

        // 🔹 예약 화면 (수정된 부분)
        composable("reservation_screen") {
            // ReservationScreen을 호출할 때 onNavigateBack 파라미터에 값을 전달합니다.
            ReservationScreen(
                onNavigateBack = {
                    // 이 함수가 호출되면, 현재 화면(ReservationScreen)을 닫고
                    // 이전 화면(StatusScreen)으로 돌아갑니다.
                    navController.popBackStack()
                }
            )
        }
    }
}