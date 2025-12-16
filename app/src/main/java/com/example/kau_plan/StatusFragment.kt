package com.example.kau_plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image // 이미지 표시를 위해 추가
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource // 리소스 불러오기를 위해 추가
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class StatusFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    StatusScreen()
                }
            }
        }
    }
}

// --- 데이터 모델 ---
data class FridgeItem(val id: String, val owner: String, val itemName: String, val date: String)
data class MachineState(val id: String, val isAvailable: Boolean)

// --- 색상 상수 ---
val KauBlue = Color(0xFF5B5BF5)
val TextDarkBlue = Color(0xFF000080)
val ProgressYellow = Color(0xFFFFC107)
val StatusRed = Color(0xFFFF5252)
val StatusGreen = Color(0xFF2196F3)

// --- 메인 스크린 (화면 전환 로직) ---
@Composable
fun StatusScreen() {
    var isDetailViewVisible by remember { mutableStateOf(false) }

    BackHandler(enabled = isDetailViewVisible) {
        isDetailViewVisible = false
    }

    if (isDetailViewVisible) {
        FridgeDetailScreen(onBackClick = { isDetailViewVisible = false })
    } else {
        DashboardContent(onFridgeDetailClick = { isDetailViewVisible = true })
    }
}

// --- 화면 1: 현황판 (Dashboard) ---
@Composable
fun DashboardContent(onFridgeDetailClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "공용 물품 사용 현황",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 이용률 카드 섹션
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 냉장고 카드 (mini.png)
            UsageCard(
                title = "냉장고 이용률",
                imageResId = R.drawable.mini,
                percentage = 0.55f,
                percentText = "55%",
                hasDetailLink = true,
                onDetailClick = onFridgeDetailClick,
                modifier = Modifier.weight(1f)
            )
            // ★ 헬스장 카드 (gym.png 로 변경)
            UsageCard(
                title = "헬스장 혼잡율",
                imageResId = R.drawable.gym, // 기존 iconVector 대신 imageResId 사용
                percentage = 0.80f,
                percentText = "80%",
                hasDetailLink = false,
                onDetailClick = {},
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 건조기 리스트
        MachineGroupCard(
            type = "건조기",
            machines = listOf(
                MachineState("A", false),
                MachineState("B", false),
                MachineState("C", true)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 세탁기 리스트
        MachineGroupCard(
            type = "세탁기",
            machines = listOf(
                MachineState("A", true),
                MachineState("B", false),
                MachineState("C", true)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- 화면 2: 냉장고 상세 리스트 ---
@Composable
fun FridgeDetailScreen(onBackClick: () -> Unit) {
    val fridgeList = listOf(
        FridgeItem("1", "김항대", "우유 1L", "10/24"),
        FridgeItem("2", "이비행", "사과 3개", "10/25"),
        FridgeItem("3", "박조종", "샌드위치", "10/26"),
        FridgeItem("4", "최관제", "물 2L", "10/26"),
        FridgeItem("5", "정기계", "케이크", "10/27"),
        FridgeItem("6", "김항대", "콜라", "10/28"),
        FridgeItem("7", "이비행", "햇반", "10/29")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "냉장고 보관 목록",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Divider(color = Color.LightGray)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(12.dp)
        ) {
            Text(text = "이름", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text(text = "물품명", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
            Text(text = "등록일", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(fridgeList) { item ->
                FridgeListItem(item)
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun FridgeListItem(item: FridgeItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.owner, modifier = Modifier.weight(1f), color = Color.Gray)
        Text(text = item.itemName, modifier = Modifier.weight(2f), fontWeight = FontWeight.Medium)
        Text(text = item.date, modifier = Modifier.weight(1f), textAlign = TextAlign.End, color = KauBlue)
    }
}

// --- 공통 컴포넌트 ---

@Composable
fun HeaderSection() {
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(color = TextDarkBlue, fontWeight = FontWeight.Bold)) {
            append("한국항공대학교")
        }
        append(" 기숙사 - A 동\n공동 물품 사용 현황입니다")
    }

    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth(),
        lineHeight = 30.sp
    )
}

// UsageCard: 이미지와 아이콘 둘 다 처리 가능
@Composable
fun UsageCard(
    title: String,
    iconVector: ImageVector? = null, // 아이콘
    imageResId: Int? = null,         // 이미지
    percentage: Float,
    percentText: String,
    hasDetailLink: Boolean,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KauBlue),
        modifier = modifier.height(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Box(contentAlignment = Alignment.Center) {
                // 이미지가 있으면 이미지를, 없으면 벡터 아이콘을 표시
                if (imageResId != null) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                } else if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    progress = percentage,
                    color = ProgressYellow,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = percentText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                if (hasDetailLink) {
                    Text(
                        text = "상세보기",
                        color = Color.White,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { onDetailClick() }
                    )
                } else {
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
fun MachineGroupCard(type: String, machines: List<MachineState>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            machines.forEach { machine ->
                MachineItem(type, machine)
            }
        }
    }
}

@Composable
fun MachineItem(type: String, machine: MachineState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            text = "$type - ${machine.id}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 이미지 적용 부분
        val imageResId = if (type == "세탁기") {
            R.drawable.washingmachine
        } else {
            R.drawable.dryer
        }

        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "$type 이미지",
            modifier = Modifier
                .size(60.dp)
                .padding(bottom = 8.dp)
        )

        val statusIcon = if (machine.isAvailable) {
            Icons.Outlined.CheckCircle
        } else {
            Icons.Default.Close
        }

        Icon(
            imageVector = statusIcon,
            contentDescription = null,
            tint = if (machine.isAvailable) StatusGreen else StatusRed,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (machine.isAvailable) {
            Text(
                text = "사용가능",
                color = StatusGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        } else {
            Text(
                text = "사용중",
                color = StatusRed,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatusScreenPreview() {
    MaterialTheme {
        StatusScreen()
    }
}