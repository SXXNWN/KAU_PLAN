package com.example.kau_plan.ui.theme.expense

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kau_plan.data.Expense
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID

class ExpenseViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val expensesCol = db.collection("expenses")
    private var listener: ListenerRegistration? = null

    private val _expenses = mutableStateListOf<Expense>()
    val expenses: List<Expense> get() = _expenses

    var monthlyGoal by mutableStateOf(500_000)

    init {
        startListening()
    }

    val monthlyTotal: Int
        get() = _expenses.sumOf { it.amount }

    private fun startListening() {
        // Firestore의 changes를 받아 로컬 상태 리스트를 갱신
        listener?.remove()
        listener = expensesCol.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener

            val remote = snapshot.documents.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                val category = doc.getString("category") ?: return@mapNotNull null
                val payer = doc.getString("payer") ?: return@mapNotNull null
                val date = doc.getString("date") ?: return@mapNotNull null
                val amountLong = doc.getLong("amount") ?: 0L

                Expense(
                    id = doc.id,
                    title = title,
                    category = category,
                    payer = payer,
                    date = date,
                    amount = amountLong.toInt()
                )
            }

            // 로컬 상태를 원격 기준으로 맞춘다.
            _expenses.clear()
            _expenses.addAll(remote)
        }
    }

    private fun toFirestoreMap(expense: Expense): Map<String, Any> {
        return mapOf(
            "title" to expense.title,
            "category" to expense.category,
            "payer" to expense.payer,
            "date" to expense.date,
            "amount" to expense.amount
        )
    }

    fun addExpense(expense: Expense) {
        val newExpense =
            if (expense.id.isBlank()) {
                expense.copy(id = UUID.randomUUID().toString())
            } else {
                expense
            }

        _expenses.add(newExpense)

        expensesCol.document(newExpense.id)
            .set(toFirestoreMap(newExpense))
    }

    fun deleteExpense(expenseId: String) {
        _expenses.removeAll { it.id == expenseId }

        if (expenseId.isNotBlank()) {
            expensesCol.document(expenseId).delete()
        }
    }

    fun updateExpense(updatedExpense: Expense) {
        val index = _expenses.indexOfFirst { it.id == updatedExpense.id }
        if (index != -1) {
            _expenses[index] = updatedExpense

            if (updatedExpense.id.isNotBlank()) {
                expensesCol.document(updatedExpense.id)
                    .set(toFirestoreMap(updatedExpense))
            }
        }
    }

    fun findExpenseById(expenseId: String): Expense? {
        return _expenses.firstOrNull { it.id == expenseId }
    }
    override fun onCleared() {
        super.onCleared()
        listener?.remove()
        listener = null
    }
}