package com.example.kau_plan.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kau_plan.data.Expense
import com.example.kau_plan.ui.theme.expense.AddExpenseScreen
import com.example.kau_plan.ui.theme.expense.ExpenseListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kau_plan.ui.theme.expense.ExpenseViewModel
import com.example.kau_plan.ui.theme.profile.ProfileScreen

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

        composable(route = "home") {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
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