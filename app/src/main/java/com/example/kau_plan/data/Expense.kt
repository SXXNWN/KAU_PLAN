package com.example.kau_plan.data

data class Expense(
    val title: String = "",
    val category: String = "",
    val payer: String = "",
    val date: String = "",
    val amount: Int = 0,
    val id: String = ""
)

val dummyExpenses = listOf(
    Expense("치킨", "식비", "정윤님", "2025.11.09", 24000),
    Expense("세제", "생활용품", "현우님", "2025.11.08", 12000),
    Expense("지하철", "교통비", "지환님", "2025.11.08", 1550),
)