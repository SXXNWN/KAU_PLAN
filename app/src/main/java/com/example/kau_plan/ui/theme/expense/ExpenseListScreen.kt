package com.example.kau_plan.ui.theme.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kau_plan.data.Expense

@Composable
fun ExpenseListScreen(
    monthlyTotal: Int,
    monthlyGoal: Int,
    expenses: List<Expense>,
    onAddClick: () -> Unit,
    onEditClick: (Expense) -> Unit = {},
    onDeleteClick: (Expense) -> Unit = {}
) {
    var keyword by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("전체") }

    val filteredExpenses by remember(keyword, selectedCategory) {
        derivedStateOf {
            expenses.filter { e ->
                val matchesCategory = (selectedCategory == "전체") || (e.category == selectedCategory)
                val matchesKeyword = keyword.isBlank() ||
                        e.title.contains(keyword, ignoreCase = true) ||
                        e.category.contains(keyword, ignoreCase = true)
                matchesCategory && matchesKeyword
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF6B4DFF)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "지출 추가",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            MonthlySummaryCard(
                monthlyTotal = monthlyTotal,
                monthlyGoal = monthlyGoal
            )

            Spacer(modifier = Modifier.height(13.dp))

            SearchAndCategoryRow(
                keyword = keyword,
                onKeywordChange = { keyword = it },
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExpenseList(
                expenses = filteredExpenses,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
fun MonthlySummaryCard(
    monthlyTotal: Int,
    monthlyGoal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B4DFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "이번 달 총 지출",
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%,d원".format(monthlyTotal),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "이번 달 목표",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = "%,d원".format(monthlyGoal),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val progress = (monthlyTotal.toFloat() / monthlyGoal.toFloat()).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndCategoryRow(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = keyword,
            onValueChange = onKeywordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            placeholder = { Text("항목명 검색") }
        )

        Spacer(modifier = Modifier.height(7.dp))

        val categories = listOf("전체", "식비", "생활용품", "교통비", "구독", "취미생활", "기타")
        val chipScrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(chipScrollState)
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val selected = category == selectedCategory
                AssistChip(
                    modifier = Modifier.height(28.dp),
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            text = category,
                            fontSize = 12.sp
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) Color(0xFF6B4DFF) else Color(0xFFF2F2F2),
                        labelColor = if (selected) Color.White else Color.DarkGray
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
fun ExpenseList(
    expenses: List<Expense>,
    onEditClick: (Expense) -> Unit,
    onDeleteClick: (Expense) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = expenses,
            key = { e -> if (e.id.isNotBlank()) e.id else "${e.title}-${e.date}-${e.amount}" }
        ) { expense ->
            ExpenseItem(
                expense = expense,
                onEditClick = { onEditClick(expense) },
                onDeleteClick = { onDeleteClick(expense) }
            )
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF2F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = expense.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = expense.category,
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${expense.payer} · ${expense.date}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "%,d원".format(expense.amount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B4DFF)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onEditClick) {     // 수정 버튼을 누르면 onEditClick 콜백 호출
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "지출 수정"
                            )
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "지출 삭제"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseListScreenPreview() {
    val sampleExpenses = listOf(
        Expense(title = "치킨", category = "식비", payer = "정윤님", date = "2025.11.09", amount = 24000),
        Expense(title = "세제", category = "생활용품", payer = "현우님", date = "2025.11.08", amount = 12000),
        Expense(title = "지하철", category = "교통비", payer = "지환님", date = "2025.11.08", amount = 1550),
        Expense(title = "택시", category = "교통비", payer = "세현님", date = "2025.11.07", amount = 10050),
        Expense(title = "F1 TV", category = "구독", payer = "세현님", date = "2025.11.06", amount = 15550),
        Expense(title = "카메라", category = "취미생활", payer = "지환님", date = "2025.11.04", amount = 90000),
    )

    ExpenseListScreen(
        monthlyTotal = 229_500,
        monthlyGoal = 500_000,
        expenses = sampleExpenses,
        onAddClick = {}
    )
}
