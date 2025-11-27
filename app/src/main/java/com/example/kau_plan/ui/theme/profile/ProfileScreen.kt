package com.example.kau_plan.ui.theme.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    // 스크롤 가능한 전체 레이아웃
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5F7))   // 배경 연한 핑크
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // 상단 프로필 카드
        ProfileHeaderCard(
            userName = "정윤님",
            roomInfo = "250호 멤버",
            monthlyExpense = 199_000,
            routineRate = 83
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 월별 생활비
        MonthlySpendingSection()

        Spacer(modifier = Modifier.height(12.dp))

        // 카테고리별 지출
        CategorySpendingSection()

        Spacer(modifier = Modifier.height(12.dp))

        // 주간 루틴 달성률
        WeeklyRoutineSection()

        Spacer(modifier = Modifier.height(12.dp))

        // 최근 활동
        RecentActivitySection()
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
            // 상단: 프로필 사진 + 이름 + 설정 아이콘
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 동그란 프로필 자리
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

            // 가운데 구분선
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0D7FF))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 하단: 이번 달 지출 / 루틴 달성률
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
private fun MonthlySpendingSection() {
    SectionCard(
        title = "월별 생활비",
        iconEmoji = "📉"
    ) {
        // 간단한 바 차트 모양 (예시용)
        val months = listOf("6월", "7월", "8월", "9월", "10월", "11월")
        val values = listOf(60, 120, 90, 150, 80, 130)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            values.forEachIndexed { index, value ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(value.dp)
                            .background(Color(0xFF6B4DFF), RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = months[index],
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySpendingSection() {
    SectionCard(
        title = "카테고리별 지출",
        iconEmoji = "📅"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 왼쪽: 간단한 도넛/파이 느낌의 원형 그래프 자리
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // 아주 간단한 색 영역 분할 (실제 비율 계산 X, 그냥 느낌용)
                    val colors = listOf(
                        Color(0xFF4E7EFF),
                        Color(0xFFFF8A80),
                        Color(0xFFFFC107),
                        Color(0xFF8BC34A),
                        Color(0xFF9C27B0),
                        Color(0xFF26C6DA)
                    )
                    var startAngle = -90f
                    val sweep = 360f / colors.size

                    colors.forEach { c ->
                        drawArc(
                            color = c,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true
                        )
                        startAngle += sweep
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 오른쪽: 범례 + 금액 리스트 (더미 데이터)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CategoryLegendRow("식비", "85,000원", Color(0xFF4E7EFF))
                CategoryLegendRow("생활용품", "56,000원", Color(0xFFFF8A80))
                CategoryLegendRow("교통비", "48,000원", Color(0xFFFFC107))
                CategoryLegendRow("구독", "24,000원", Color(0xFF8BC34A))
                CategoryLegendRow("취미생활", "97,000원", Color(0xFF9C27B0))
                CategoryLegendRow("기타", "42,000원", Color(0xFF26C6DA))
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
private fun WeeklyRoutineSection() {
    SectionCard(
        title = "주간 루틴 달성률",
        iconEmoji = "🏁"
    ) {
        val days = listOf("월", "화", "수", "목", "금", "토", "일")
        val values = listOf(90, 80, 70, 50, 40, 30, 20)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            values.forEachIndexed { index, value ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(value.dp)
                            .background(Color(0xFF5E8C5A), RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = days[index],
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection() {
    SectionCard(
        title = "최근 활동",
        iconEmoji = "📝"
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("• 11/10  치킨 배달비 24,000원", fontSize = 12.sp)
            Text("• 11/09  세제 구입 12,000원", fontSize = 12.sp)
            Text("• 11/07  택시비 10,050원", fontSize = 12.sp)
            Text("• 11/06  OTT 구독료 15,550원", fontSize = 12.sp)
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}