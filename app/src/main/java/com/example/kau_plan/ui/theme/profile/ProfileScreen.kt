package com.example.kau_plan.ui.theme.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    monthlyTotal: Int,
    expenses: List<com.example.kau_plan.data.Expense>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5F7))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        ProfileHeaderCard(
            userName = "정윤님",
            roomInfo = "250호 멤버",
            monthlyExpense = monthlyTotal,
            routineRate = 83
        )

        Spacer(modifier = Modifier.height(12.dp))

        val monthlyTotals = remember(expenses) {
            computeMonthlyTotals(expenses)
        }
        MonthlySpendingSection(monthlyTotals = monthlyTotals)

        Spacer(modifier = Modifier.height(12.dp))

        val categoryTotals = remember(expenses) {
            computeCategoryTotals(expenses)
        }
        CategorySpendingSection(categoryTotals = categoryTotals)

        Spacer(modifier = Modifier.height(12.dp))

        val recentExpenses = remember(expenses) {
            expenses
                .sortedByDescending { it.date }
                .take(4)
        }
        RecentActivitySection(recentExpenses = recentExpenses)
    }
}

@Composable
private fun ProfileHeaderCard(
    userName: String,
    roomInfo: String,
    monthlyExpense: Int,
    routineRate: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B4DFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFEDE7FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🙂", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = roomInfo,
                        fontSize = 14.sp,
                        color = Color(0xFFE0D7FF)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0D7FF))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "이번 달 지출",
                        fontSize = 14.sp,
                        color = Color(0xFFE0D7FF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%,d원".format(monthlyExpense),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "루틴 달성률",
                        fontSize = 14.sp,
                        color = Color(0xFFE0D7FF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$routineRate%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlySpendingSection(
    monthlyTotals: Map<Int, Int>
) {
    SectionCard(
        title = "월별 생활비",
        iconEmoji = "📉"
    ) {
        val months = (1..12).toList()
        val values = months.map { month -> monthlyTotals[month] ?: 0 }

        val chartHeight = 120.dp
        val maxValue = values.maxOrNull() ?: 0

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(months) { month ->
                val value = monthlyTotals[month] ?: 0
                val ratio = if (maxValue == 0) 0f else value.toFloat() / maxValue
                val barHeight = (120 * ratio).dp

                Column(
                    modifier = Modifier.width(44.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .height(chartHeight)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height(barHeight)
                                .background(Color(0xFF6B4DFF), RoundedCornerShape(6.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${month}월",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySpendingSection(
    categoryTotals: Map<String, Int>
) {
    SectionCard(
        title = "카테고리별 지출",
        iconEmoji = "📅"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val total = categoryTotals.values.sum().toFloat()
                    var startAngle = -90f

                    val colorMap = mapOf(
                        "식비" to Color(0xFF4E7EFF),
                        "생활용품" to Color(0xFFFF8A80),
                        "교통비" to Color(0xFFFFC107),
                        "구독" to Color(0xFF8BC34A),
                        "취미생활" to Color(0xFF9C27B0),
                        "기타" to Color(0xFF26C6DA)
                    )

                    categoryTotals.forEach { (category, amount) ->
                        val sweepAngle = if (total == 0f) 0f else (amount / total) * 360f
                        val color = colorMap[category] ?: Color.LightGray
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                        startAngle += sweepAngle
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categoryTotals.forEach { (category, amount) ->
                    val color = when (category) {
                        "식비" -> Color(0xFF4E7EFF)
                        "생활용품" -> Color(0xFFFF8A80)
                        "교통비" -> Color(0xFFFFC107)
                        "구독" -> Color(0xFF8BC34A)
                        "취미생활" -> Color(0xFF9C27B0)
                        "기타" -> Color(0xFF26C6DA)
                        else -> Color.Gray
                    }

                    CategoryLegendRow(
                        name = category,
                        amount = "%,d원".format(amount),
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryLegendRow(
    name: String,
    amount: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = amount,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun RecentActivitySection(
    recentExpenses: List<com.example.kau_plan.data.Expense>
) {
    SectionCard(
        title = "최근 활동",
        iconEmoji = "📝"
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (recentExpenses.isEmpty()) {
                Text(
                    text = "최근 지출 내역이 없습니다.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            recentExpenses.forEach { expense ->
                Text(
                    text = "• ${expense.date}  ${expense.title} ${"%,d원".format(expense.amount)}",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    iconEmoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFBEAF3)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

private fun computeMonthlyTotals(
    expenses: List<com.example.kau_plan.data.Expense>
): Map<Int, Int> {
    return expenses.groupBy { expense ->
        // date format: yyyy.MM.dd (e.g., 2025.12.04)
        expense.date.split(".")[1].toInt()
    }.mapValues { entry ->
        entry.value.sumOf { it.amount }
    }
}

private fun computeCategoryTotals(
    expenses: List<com.example.kau_plan.data.Expense>
): Map<String, Int> {
    return expenses.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        monthlyTotal = 199_000,
        expenses = listOf(
            com.example.kau_plan.data.Expense(
                title = "치킨",
                category = "식비",
                payer = "정윤님",
                date = "2025.11.09",
                amount = 24000
            ),
            com.example.kau_plan.data.Expense(
                title = "세제",
                category = "생활용품",
                payer = "정윤님",
                date = "2025.12.04",
                amount = 12000
            )
        )
    )
}