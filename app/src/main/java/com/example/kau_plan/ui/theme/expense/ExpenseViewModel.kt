package com.example.kau_plan.ui.theme.expense

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kau_plan.data.Expense

class ExpenseViewModel : ViewModel() {

    // 지출 목록을 담는 상태 리스트
    private val _expenses = mutableStateListOf<Expense>()
    val expenses: List<Expense> get() = _expenses

    // 월 목표 금액 (예: 500,000원)
    var monthlyGoal by mutableStateOf(500_000)

    // 지출 총합 (계산용)
    val monthlyTotal: Int
        get() = _expenses.sumOf { it.amount }

    // 지출 추가 함수
    fun addExpense(expense: Expense) {
        _expenses.add(expense)
    }
}
