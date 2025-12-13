package com.example.kau_plan.ui.theme.expense           // 이 파일이 어느 패키지에 속해 있는지 알려주는 코드

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

// 1) 소비내역 메인 화면
@Composable
fun ExpenseListScreen(
    monthlyTotal: Int,              // 상단 카드에 보여줄 이번 달 총 지출
    monthlyGoal: Int,               // 상단 카드에 보여줄 이번 달 목표 지출
    expenses: List<Expense>,        // expenses = 사용한 지출을 보여주는 리스트에서 사용할 지출 데이터 목록
    onAddClick: () -> Unit          // 우측 하단에 위치한 + 버튼을 눌렀을 때 실행할 동작(지출 추가 화면 열기 등), onAddClick은 부모로부터 함수를 파라미터로 받는다
) {
    var keyword by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("전체") }

    // ✅ 검색/카테고리 필터 적용
    val filteredExpenses = remember(expenses, keyword, selectedCategory) {
        expenses.filter { e ->
            val matchesCategory = (selectedCategory == "전체") || (e.category == selectedCategory)
            val matchesKeyword = keyword.isBlank() ||
                    e.title.contains(keyword, ignoreCase = true) ||
                    e.category.contains(keyword, ignoreCase = true)
            matchesCategory && matchesKeyword
        }
    }

    Scaffold(
        floatingActionButton = {                            // 우측 하단에 떠 있는 + 버튼을 정의하는 부분
            FloatingActionButton(                           // 실제로 동그란 버튼에 해당하는 FAB를 생성하는 Composable 함수
                onClick = onAddClick,                       // 버튼이 눌릴 때 onClick이 호출되고 onClick에 넣어둔 onAddClick 파라미터가 가진 함수가 실행됨
                containerColor = Color(0xFF6B4DFF)   // 버튼의 배경색을 보라색으로 설정
            ) {
                Icon(                                       // 동그란 버튼 안에 있는 + 아이콘을 정의하는 부분
                    imageVector = Icons.Default.Add,
                    contentDescription = "지출 추가",
                    tint = Color.White                      // + 아이콘의 색을 흰색으로 설정
                )
            }
        }
    ) { innerPadding ->                                         // {innerPadding -> ....} = Scffold 내부를 구성하는 람다함수, innerPadding = Scaffold 함수가 만들어서 넘겨주는 여백 값을 받는 람다함수의 파라미터이다.
        Column(                                                 // Column = 세로로 위에서 아래로 쌓는 레이아웃
            modifier = Modifier
                .fillMaxSize()                                  // 화면 전체 사용하도록 설정
                .padding(innerPadding)           // 생성할 패딩을 innerPadding 타입으로 설
                .padding(horizontal = 16.dp, vertical = 12.dp)  // Column의 좌우에 16dp의 패딩 추가, Column의 상하에 12dp의 패딩 추가
        ) {
            MonthlySummaryCard(                             // MonthlySummaryCard = 달별 지출을 표기하는 상단 파란 카드 영역을 화면에 그리는 Composable 함수
                monthlyTotal = monthlyTotal,
                monthlyGoal = monthlyGoal
            )

            Spacer(modifier = Modifier.height(13.dp))   // 위/아래 컴포넌트 사이에 16dp의 빈공간을 넣는 코드

            SearchAndCategoryRow(       // 검색 창과 카테고리 버튼들을 화면에 그리는 Composable 함수 호출

                keyword = keyword,
                onKeywordChange = { keyword = it },
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(12.dp))   // 위/아래 컴포넌트 사이에 12dp의 빈 공간을 넣는 코드

            ExpenseList(expenses = filteredExpenses)                // 카테고리 버튼 아래에 있는 지출 목록을 화면에 출력하는 Composable 함수 호출
        }
    }
}

// 2) 상단 "이번 달 총 지출" 카드
@Composable
fun MonthlySummaryCard(             // 소비내역 페이지의 최상단에 위치하여 이번달 지출을 표시하는 보라색 카드를 생성하는 Composable 함수
    monthlyTotal: Int,
    monthlyGoal: Int
) {
    Card(                                           // Card = 박스 형태로 UI를 담는 Material Container에 해당됨, 그중 그림자 + 둥근 모서리를 가진 박스를 생성함
        modifier = Modifier.fillMaxWidth(),         // Card 박스의 크기는 Modifier가 결정함, fillMaxWidth = Card 박스의 가로 길이를 부모인 Column의 폭으로 설정함, Modifier의 세로 높이 속성은 설정하지 않고 있으므로 Card 박스가 가지는 Composable들의 배치에 따라 정해짐
        shape = RoundedCornerShape(20.dp),    // 모서리를 20dp만큼 둥글게 설정
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B4DFF)  // 카드 배경색을 의미하는 containerColor를 보라색으로 설정
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)     // elevation = Card 박스를 위로 4dp 만큼 떠보이게 만들어 그림자가 생긱게함
    ) {
        Column(                         // Column 내부의 모든 Composable들(Text, Row, Spacer)을 위에서 아래 방향으로 쌓아 내려가는 박스
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)     // Column의 상하좌우에 20dp 크기의 패딩을 적용하여 Card 박스로부터 Column이 20dp 안쪽으로 들어와 있도록 만든다.
        ) {
            Text(                       // Text = 텍스트를 표시하는 Composable
                text = "이번 달 총 지출",  // 표기할 문자열을 지정
                color = Color.White,    // 텍스트의 색을 흰색으로 지정
                fontSize = 14.sp        // 텍스트의 크기를 14sp로 지정
            )
            Spacer(modifier = Modifier.height(4.dp))    // 앞서 나온 Text와 아래에 있는 Text 사이에 높이 4dp의 여백을 넣는 역할을 함
            Text(
                text = "%,d원".format(monthlyTotal),     // monthlyTotal 숫자를 ,가 있는 형태로 표기함 -> monthlyTotal = 229500이라면 "229,500원"으로 표기함
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold                    // 텍스트를 굵게(Bold) 표기함
            )

            Spacer(modifier = Modifier.height(16.dp))   // 위에 있는 Text와 아래에 오는 Row 사이에 높이 16dp의 여백을 넣음

            Row(                                                    // Row는 Row 내부에 있는 composable인 두개의 Text를 가로로 배열하는 박스로 볼 수 있음
                modifier = Modifier.fillMaxWidth(),                 // Row가 부모인 Column의 전체 폭을 다 사용하도록 설정
                horizontalArrangement = Arrangement.SpaceBetween    // Arrangement를 통해 Row가 가지는 composable 사이의 가로 간격을 설정함, SpaceBetween은 각 composable 사이에 균등한 간격을 두도록함, 여기서는 각 Text가 좌측 끝과 우측 끝에 위치하게 됨
            ) {
                Text(                                               // Card 박스의 좌측에 "이번 달 목표"가 출력됨
                    text = "이번 달 목표",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(                                               // Card 박스 우측 끝에 이번 달 목표 지출 수치가 출력됨
                    text = "%,d원".format(monthlyGoal),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val progress = (monthlyTotal.toFloat() / monthlyGoal.toFloat())     // progress = 여태까지 이번 달 목표 지출의 몇 퍼센트를 사용했는지 알려주는 수치
                .coerceIn(0f, 1f)                                               // Progress 값을 float(0f ~ 1f) 범위의 값으로 설정

            Box(                                                                // 이번 달의 목표 지출 중 사용 비율을 시각적으로 보여주는 프로그레스 바에서 배경인 회색 부분을 위한 Box
                modifier = Modifier
                    .fillMaxWidth()                                             // Modifier의 폭을 fillMaxWidth로 설정하여 Box가 부모인 Column과 동일한 폭을 가지도록 함
                    .height(8.dp)                                       // Modifier의 높이를 8dp로 설정하여 Box의 높이를 8dp로 설정함
                    .clip(RoundedCornerShape(999.dp))             // clip = Box의 모서리를 RoundedCornerShape으로 둥글게 자르는 역할 수행, 999.dp = 모서리의 곡률
                    .background(Color.White.copy(alpha = 0.3f))                 // White = Box의 배경색, alpha = 색의 투명도를 설정 -> 옆은 회색이 됨
            ) {
                Box(                                                            // 프로그레스 바에서 이번달 사용한 지출을 표현하기 위한 Box
                    modifier = Modifier
                        .fillMaxHeight()                                        // Modifier의 높이를 fillMaxHeight로 설정하여 Box의 높이를 부모인 Box의 높이와 동일하게 설정
                        .fillMaxWidth(progress)                        // Modifier의 폭을
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White)
                )
            }
        }
    }
}

// 3) 검색창 + 카테고리 칩 줄
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndCategoryRow(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {                                            // 지출 검색창과 카테고리 종류를 화면에 표시하는 Composable 함수
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

        // ✅ 가로 스크롤 상태 (카테고리 칩이 화면 밖으로 넘어가면 좌우로 스크롤 가능)
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
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
                        )
                            },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) Color(0xFF6B4DFF) else Color(0xFFF2F2F2),
                        labelColor = if (selected) Color.White else Color.DarkGray
                    ),
                    border = null
                )


            //                AssistChip(
//                    onClick = { selectedCategory = category },
//                    label = { Text(category, fontSize = 9.sp) },
//                    colors = AssistChipDefaults.assistChipColors(
//                        containerColor = if (selected) Color(0xFF6B4DFF) else Color(0xFFF2F2F2),
//                        labelColor = if (selected) Color.White else Color.DarkGray
//                    )
//                )
            }
        }
    }
}

// 4) 지출 항목 리스트
@Composable
fun ExpenseList(expenses: List<Expense>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(expenses) { expense ->
            ExpenseItem(expense = expense)
        }
    }
}

// 5) 지출 하나를 보여주는 카드
@Composable
fun ExpenseItem(expense: Expense) {
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

                Text(
                    text = "%,d원".format(expense.amount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4DFF)
                )
            }
        }
    }
}

// 6) 미리보기용 Preview (앱 테스트할 때 편하게 보려고)
@Preview(showBackground = true)
@Composable
fun ExpenseListScreenPreview() {
    val sampleExpenses = listOf(
        Expense("치킨", "식비", "정윤님", "2025.11.09", 24000),
        Expense("세제", "생활용품", "현우님", "2025.11.08", 12000),
        Expense("지하철", "교통비", "지환님", "2025.11.08", 1550),
        Expense("택시", "교통비", "세현님", "2025.11.07", 10050),
        Expense("F1 TV", "구독", "세현님", "2025.11.06", 15550),
        Expense("카메라", "취미생활", "지환님", "2025.11.04", 90000),
    )

    ExpenseListScreen(
        monthlyTotal = 229_500,
        monthlyGoal = 500_000,
        expenses = sampleExpenses,
        onAddClick = {}
    )
}
