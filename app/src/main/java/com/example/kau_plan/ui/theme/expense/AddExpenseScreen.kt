package com.example.kau_plan.ui.theme.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.kau_plan.data.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    expenseToEdit: Expense? = null,
    onSaveClick: (Expense) -> Unit,
    onCancelClick: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("식비") }
    var selectedPayer by rememberSaveable { mutableStateOf("정윤님") }
    var date by rememberSaveable { mutableStateOf("") }
    var memo by rememberSaveable { mutableStateOf("") }


    LaunchedEffect(expenseToEdit?.id) {
        if (expenseToEdit != null) {
            title = expenseToEdit.title
            amountText = expenseToEdit.amount.toString()
            selectedCategory = expenseToEdit.category
            selectedPayer = expenseToEdit.payer
            date = expenseToEdit.date
            memo = expenseToEdit.memo
        } else {
            title = ""
            amountText = ""
            selectedCategory = "식비"
            selectedPayer = "정윤님"
            date = ""
            memo = ""
        }
    }

    val categories = listOf("식비", "생활용품", "교통비", "구독", "취미생활", "기타")
    val payers = listOf("정윤님", "지환님", "세현님", "현우님")
    val isEditMode = expenseToEdit != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditMode) "지출 수정" else "지출 추가",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("항목명") },
            singleLine = true
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("금액") },
            singleLine = true
        )

        Text(
            text = "카테고리",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        val categoryScroll = rememberScrollState()
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(categoryScroll)
        ) {
            categories.forEach { category ->
                val selected = category == selectedCategory
                AssistChip(
                    modifier = Modifier.height(24.dp),
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) Color(0xFF6B4DFF) else Color(0xFFF2F2F2),
                        labelColor = if (selected) Color.White else Color.DarkGray
                    )
                )
            }
        }

        Text(
            text = "결제자",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            payers.forEach { payer ->
                val selected = payer == selectedPayer
                AssistChip(
                    onClick = { selectedPayer = payer },
                    label = { Text(payer, fontSize = 12.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) Color(0xFF6B4DFF) else Color(0xFFF2F2F2),
                        labelColor = if (selected) Color.White else Color.DarkGray
                    )
                )
            }
        }

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("날짜 (예: 2025.11.10)") },
            singleLine = true
        )

        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            label = { Text("메모") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("취소")
            }

            Button(
                onClick = {
                    val amount = amountText.toIntOrNull() ?: 0
                    val expense = Expense(
                        title = title,
                        category = selectedCategory,
                        payer = selectedPayer,
                        date = date,
                        amount = amount,
                        memo = memo,
                        id = expenseToEdit?.id ?: ""
                    )
                    onSaveClick(expense)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isEditMode) "수정" else "저장")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    AddExpenseScreen(
        expenseToEdit = null,
        onSaveClick = {},
        onCancelClick = {}
    )
}