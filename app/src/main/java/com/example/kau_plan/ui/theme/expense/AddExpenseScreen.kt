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
    onSaveClick: (Expense) -> Unit,      // 저장 버튼 눌렀을 때 호출할 함수 (부모에서 전달)
    onCancelClick: () -> Unit           // 취소 버튼 눌렀을 때 호출할 함수 (부모에서 전달)
) {
    // 🔹 1) 각 입력 칸에 대응되는 상태 변수들
    var title by remember { mutableStateOf("") }          // 항목명
    var amountText by remember { mutableStateOf("") }     // 금액 (문자열로 입력받고 나중에 Int로 변환)
    var selectedCategory by remember { mutableStateOf("식비") } // 선택된 카테고리
    var selectedPayer by remember { mutableStateOf("정윤님") }  // 선택된 결제자
    var date by remember { mutableStateOf("") }           // 날짜 (간단히 문자열로 입력)
    var memo by remember { mutableStateOf("") }           // 메모

    val categories = listOf("식비", "생활용품", "교통비", "구독", "취미생활", "기타") // 소비내역 화면 카테고리(필터용 "전체" 제외)
    val payers = listOf("정윤님", "지환님", "세현님", "현우님")

    // 화면이 작아지거나 키보드가 올라와도 스크롤 가능하도록 세로 스크롤 추가
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),   // 세로 방향 스크롤 가능
        verticalArrangement = Arrangement.spacedBy(12.dp) // 각 요소 사이 간격 12dp
    ) {
        // 🔹 화면 제목
        Text(
            text = "지출 추가",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // 🔹 항목명 입력
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("항목명") },
            singleLine = true
        )

        // 🔹 금액 입력
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("금액") },
            singleLine = true
        )

        // 🔹 카테고리 선택
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
                .horizontalScroll(categoryScroll)        ) {
            categories.forEach { category ->
                val selected = category == selectedCategory
                AssistChip(
                    modifier = Modifier.height(24.dp),
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp)
                        )
                    },                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) Color(0xFF6B4DFF) else Color(0xFFF2F2F2),
                        labelColor = if (selected) Color.White else Color.DarkGray
                    )
                )
            }
        }

        // 🔹 결제자 선택
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

        // 🔹 날짜 입력
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("날짜 (예: 2025.11.10)") },
            singleLine = true
        )

        // 🔹 메모 입력
        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            label = { Text("메모") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 하단 취소 / 저장 버튼 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 취소 버튼
            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("취소")
            }

            // 저장 버튼
            Button(
                onClick = {
                    // amountText를 Int로 변환 (숫자가 아니면 0으로 처리)
                    val amount = amountText.toIntOrNull() ?: 0

                    // Expense 객체 생성
                    val expense = Expense(
                        title = title,
                        category = selectedCategory,
                        payer = selectedPayer,
                        date = date,
                        amount = amount
                    )

                    onSaveClick(expense)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("저장")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    AddExpenseScreen(
        onSaveClick = {},        // 미리보기니까 비워둠
        onCancelClick = {}       // 미리보기니까 비워둠
    )
}