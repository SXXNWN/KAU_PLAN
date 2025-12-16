package com.example.kau_plan

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// --- 데이터 모델 ---
data class FridgeItem(val id: String, val owner: String, val itemName: String, val date: String)

// --- 화면 상태 관리 Enum ---
enum class StatusScreenType {
    DASHBOARD,
    FRIDGE_DETAIL,
    RESERVATION_DETAIL
}

// --- 색상 상수 ---
val KauBlue = Color(0xFF5B5BF5)
val TextDarkBlue = Color(0xFF000080)
val ProgressYellow = Color(0xFFFFC107)
val StatusRed = Color(0xFFFF5252)
val StatusGreen = Color(0xFF2196F3)

// --- 메인 스크린 ---
@Composable
fun StatusScreen(
    onNavigateToReservation: () -> Unit,
    statusViewModel: StatusViewModel = viewModel()
) {
    // ★ 화면 상태 관리 (현황판 / 냉장고상세 / 예약상세)
    var currentScreen by remember { mutableStateOf(StatusScreenType.DASHBOARD) }

    // ViewModel 상태 구독
    val reservations by statusViewModel.reservations
    val machineStatus by statusViewModel.machineStatus
    val fridgeItems by statusViewModel.fridgeItems

    // 다이얼로그 상태 관리
    var showDialog by remember { mutableStateOf(false) }
    var selectedMachineName by remember { mutableStateOf("") }

    // 다이얼로그 UI
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "사용 확인", fontWeight = FontWeight.Bold) },
            text = { Text(text = "'$selectedMachineName'을(를) 지금 사용하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        statusViewModel.useMachineNow(selectedMachineName)
                        showDialog = false
                    }
                ) {
                    Text("예", color = KauBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("아니요", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        floatingActionButton = {
            // 현황판일 때만 플로팅 버튼 표시
            if (currentScreen == StatusScreenType.DASHBOARD) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToReservation,
                    containerColor = KauBlue,
                    contentColor = Color.White,
                    text = { Text("예약하기", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "예약하기") }
                )
            }
        }
    ) { innerPadding ->
        // 뒤로가기 핸들링 (상세화면 -> 현황판)
        BackHandler(enabled = currentScreen != StatusScreenType.DASHBOARD) {
            currentScreen = StatusScreenType.DASHBOARD
        }

        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                StatusScreenType.DASHBOARD -> {
                    DashboardContent(
                        onFridgeDetailClick = { currentScreen = StatusScreenType.FRIDGE_DETAIL },
                        onReservationDetailClick = { currentScreen = StatusScreenType.RESERVATION_DETAIL },
                        reservations = reservations,
                        machineStatus = machineStatus,
                        currentFridgeCount = fridgeItems.size,
                        onMachineClick = { machineName ->
                            selectedMachineName = machineName
                            showDialog = true
                        }
                    )
                }
                StatusScreenType.FRIDGE_DETAIL -> {
                    FridgeDetailScreen(
                        onBackClick = { currentScreen = StatusScreenType.DASHBOARD },
                        fridgeList = fridgeItems
                    )
                }
                StatusScreenType.RESERVATION_DETAIL -> {
                    // ★ 새로 추가된 예약 상세 화면
                    ReservationDetailScreen(
                        onBackClick = { currentScreen = StatusScreenType.DASHBOARD },
                        reservationList = reservations
                    )
                }
            }
        }
    }
}

// --- 화면 1: 현황판 (Dashboard) ---
@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    onFridgeDetailClick: () -> Unit,
    onReservationDetailClick: () -> Unit, // ★ 예약 상세 클릭 콜백 추가
    reservations: List<SimpleReservation>,
    machineStatus: MachineStatus,
    currentFridgeCount: Int,
    onMachineClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val progressValue = (currentFridgeCount / 100f).coerceIn(0f, 1f)
    val progressText = "${currentFridgeCount}%"

    Column(
        modifier = modifier
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 냉장고 카드
            UsageCard(
                title = "냉장고 이용률",
                imageResId = R.drawable.mini,
                percentage = progressValue,
                percentText = progressText,
                hasDetailLink = true,
                onDetailClick = onFridgeDetailClick,
                modifier = Modifier.weight(1f)
            )
            // ★ 예약 현황 카드 (수정됨)
            ReservationStatusCard(
                reservationCount = reservations.size, // 개수만 전달
                onDetailClick = onReservationDetailClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        MachineGroupCard(
            type = "건조기",
            machines = listOf(
                !machineStatus.isDryerA_InUse,
                !machineStatus.isDryerB_InUse,
                !machineStatus.isDryerC_InUse
            ),
            onMachineClick = onMachineClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        MachineGroupCard(
            type = "세탁기",
            machines = listOf(
                !machineStatus.isWasherA_InUse,
                !machineStatus.isWasherB_InUse,
                !machineStatus.isWasherC_InUse
            ),
            onMachineClick = onMachineClick
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// --- 실시간 예약 현황 카드 (수정됨: 요약형) ---
@Composable
fun ReservationStatusCard(
    reservationCount: Int,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)), // 회색 배경 유지
        modifier = modifier.height(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "실시간 예약 현황",
                color = TextDarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // 중앙 아이콘 및 텍스트
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = TextDarkBlue,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (reservationCount == 0) {
                    Text("예약 없음", color = Color.Gray, fontSize = 14.sp)
                } else {
                    Text(
                        text = "$reservationCount 건 예약 중",
                        color = TextDarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            // 상세보기 링크
            Text(
                text = "상세보기",
                color = TextDarkBlue,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { onDetailClick() }
            )
        }
    }
}

// --- 화면 2: 냉장고 상세 리스트 ---
@Composable
fun FridgeDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    fridgeList: List<FridgeItem>
) {
    Column(
        modifier = modifier
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

        if (fridgeList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "등록된 물품이 없습니다.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(fridgeList) { item ->
                    FridgeListItem(item)
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
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

// --- 화면 3: ★ 예약 상세 리스트 (새로 추가됨) ---
@Composable
fun ReservationDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    reservationList: List<SimpleReservation>
) {
    Column(
        modifier = modifier
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
                text = "실시간 예약 목록",
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
            Text(text = "예약 물품", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
            // 예약은 시간이 중요하므로 필요하다면 Time 필드를 추가할 수 있습니다.
            // 현재 SimpleReservation에는 시간이 없으므로 ID나 다른걸 표시하거나 생략합니다.
        }

        if (reservationList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "현재 예약된 내역이 없습니다.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(reservationList) { item ->
                    ReservationListItem(item)
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun ReservationListItem(item: SimpleReservation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.studentName, modifier = Modifier.weight(1f), color = Color.Gray)

        // 아이콘과 텍스트를 같이 표시
        Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
            val iconRes = when {
                item.itemTitle.contains("세탁기") -> R.drawable.washingmachine
                item.itemTitle.contains("건조기") -> R.drawable.dryer
                else -> R.drawable.mini
            }
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = item.itemTitle, fontWeight = FontWeight.Medium)
        }
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

@Composable
fun UsageCard(
    title: String,
    iconVector: ImageVector? = null,
    imageResId: Int? = null,
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
fun MachineGroupCard(
    type: String,
    machines: List<Boolean>,
    onMachineClick: (String) -> Unit
) {
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
            listOf("A", "B", "C").zip(machines).forEach { (id, isAvailable) ->
                MachineItem(
                    type = type,
                    id = id,
                    isAvailable = isAvailable,
                    onClick = onMachineClick
                )
            }
        }
    }
}

@Composable
fun MachineItem(
    type: String,
    id: String,
    isAvailable: Boolean,
    onClick: (String) -> Unit
) {
    val fullName = "$type - $id"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable(enabled = isAvailable) { onClick(fullName) }
    ) {
        Text(
            text = fullName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        val imageResId = if (type == "세탁기") R.drawable.washingmachine else R.drawable.dryer
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "$type 이미지",
            modifier = Modifier
                .size(60.dp)
                .padding(bottom = 8.dp)
        )
        val statusIcon = if (isAvailable) Icons.Outlined.CheckCircle else Icons.Default.Close
        Icon(
            imageVector = statusIcon,
            contentDescription = null,
            tint = if (isAvailable) StatusGreen else StatusRed,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isAvailable) "사용가능" else "사용중",
            color = if (isAvailable) StatusGreen else StatusRed,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusScreenPreview() {
    MaterialTheme {
        StatusScreen(onNavigateToReservation = {})
    }
}