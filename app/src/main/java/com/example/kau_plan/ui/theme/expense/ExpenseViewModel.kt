package com.example.kau_plan.ui.theme.expense

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kau_plan.data.Expense
import java.util.UUID

class ExpenseViewModel : ViewModel() {

    private val _expenses = mutableStateListOf<Expense>()
    val expenses: List<Expense> get() = _expenses

    var monthlyGoal by mutableStateOf(500_000)

    val monthlyTotal: Int
        get() = _expenses.sumOf { it.amount }

    fun addExpense(expense: Expense) {
        val newExpense =
            if (expense.id.isBlank()) {
                expense.copy(id = UUID.randomUUID().toString())
            } else {
                expense
            }

        _expenses.add(newExpense)
    }

    fun deleteExpense(expenseId: String) {
        _expenses.removeAll { it.id == expenseId }
    }

    fun updateExpense(updatedExpense: Expense) {
        val index = _expenses.indexOfFirst { it.id == updatedExpense.id }
        if (index != -1) {
            _expenses[index] = updatedExpense
        }
    }

    fun findExpenseById(expenseId: String): Expense? {
        return _expenses.firstOrNull { it.id == expenseId }
    }
}