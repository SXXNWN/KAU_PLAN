package com.example.kau_plan.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kau_plan.data.Expense
import com.example.kau_plan.ui.theme.expense.AddExpenseScreen
import com.example.kau_plan.ui.theme.expense.ExpenseListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kau_plan.ui.theme.expense.ExpenseViewModel

@Composable
fun AppNavigation() {

    // 네비게이션 컨트롤러 생성
    val navController = rememberNavController()

    val viewModel: ExpenseViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "expense_list"
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
    }
}