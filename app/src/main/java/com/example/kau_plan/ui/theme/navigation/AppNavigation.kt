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
import androidx.navigation.NavType
import androidx.navigation.navArgument

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

        composable("expense_list") {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                },
                onEditClick = { expense ->
                    navController.navigate("add_expense?expenseId=${expense.id}")
                },
                onDeleteClick = { expense ->
                    viewModel.deleteExpense(expense.id)
                }
            )
        }

        composable(
            route = "add_expense?expenseId={expenseId}",
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")
            val expenseToEdit = expenseId?.let { viewModel.findExpenseById(it) }

            AddExpenseScreen(
                expenseToEdit = expenseToEdit,
                onSaveClick = { expense: Expense ->
                    if (expense.id.isBlank()) {
                        viewModel.addExpense(expense)
                    } else {
                        viewModel.updateExpense(expense)
                    }
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
                },
                onEditClick = { expense ->
                    navController.navigate("add_expense?expenseId=${expense.id}")
                },
                onDeleteClick = { expense ->
                    viewModel.deleteExpense(expense.id)
                }
            )
        }

        composable(route = "profile") {
            ProfileScreen(
                monthlyTotal = viewModel.monthlyTotal,
                expenses = viewModel.expenses
            )
        }
    }
}