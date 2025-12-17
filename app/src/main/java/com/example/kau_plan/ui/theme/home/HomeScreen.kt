package com.example.kau_plan.ui.theme.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kau_plan.ui.theme.KauplanHomeScreenTheme
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.annotation.RequiresApi
import android.os.Build
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O) // API 레벨 경고 해결을 위한 어노테이션 추가 (API 26 이상)
@Composable // UI 선언
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    // ViewModel로부터 데이터 상태를 가져옴
    val commonList by homeViewModel.commonList.collectAsState()
    val personalList by homeViewModel.personalList.collectAsState()
    val progress by homeViewModel.dailyRoutineProgress.collectAsState(initial = 0f) // 초기값 0

    KauplanHomeScreenTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeHeader(navController = navController)
                UserProfileCard()
                // DailyRoutineCard에 ViewModel의 progress 전달
                DailyRoutineCard(progress = progress)
                // TodoListCard에 ViewModel의 데이터와 함수 전달
                TodoListCard(
                    commonList = commonList,
                    personalList = personalList,
                    onItemCheckedChange = { item, isCommon ->
                        homeViewModel.updateCheckedState(item, isCommon)
                    },
                    onSave = { newCommon, newPersonal ->
                        homeViewModel.saveLists(newCommon, newPersonal)
                    }
                )
            }
        }
    }
}

// 화면 상단 날짜와 아이콘 버튼 헤더
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeHeader(navController: NavHostController) {
    // 날짜 로직
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)
    val formattedDate = currentDate.format(formatter)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { navController.navigate("profile") {
                // 화면 전환 스택 정리
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            } }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "프로필",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(onClick = {  }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// 사용자 프로필, 생활비 현황
@Composable
fun UserProfileCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "사용자 아이콘",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    // 회원가입 미구현으로 인한 하드코딩
                    Text("정운님", style = MaterialTheme.typography.headlineSmall)
                    Text("250호 멤버", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("11월 생활비 현황", style = MaterialTheme.typography.titleMedium)
                    Text("500,000원", style = MaterialTheme.typography.bodyMedium)
                }

                LinearProgressIndicator(
                    progress = { 0.35f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("35% 사용", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text("잘 절약하는 중이네요! 😊", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


// 하루 루틴 달성률 원형 프로그레스
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyRoutineCard(progress: Float) {
    val currentTime = LocalTime.now()
    val timeFormatter = DateTimeFormatter.ofPattern("a h시").withLocale(Locale.forLanguageTag("ko"))
    val formattedTime = currentTime.format(timeFormatter)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("하루 루틴 달성률", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CircularProgressBar(percentage = progress, radius = 48.dp) // ViewModel의 progress 사용
                Column {
                    // Text에 포맷된 시간을 적용
                    Text("${formattedTime}까지 ${(progress * 100).toInt()}% 달성!", style = MaterialTheme.typography.titleMedium)
                    val subtitle = if (progress < 0.5f) "아직 할 일이 많아요! 😢" else "잘하고 있어요! 👍"
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

// 원형 프로그레스 바
@Composable
fun CircularProgressBar(percentage: Float, radius: Dp) {
    val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    val progressColor = MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 기본 빈 프로그래스바
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 24f)
            )
            // 진행률
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360 * percentage,
                useCenter = false,
                style = Stroke(width = 24f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = textColor
            )
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = progressColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


// 공동/개인 리스트
@Composable
fun TodoListCard(
    commonList: List<TodoItem>,
    personalList: List<TodoItem>,
    onItemCheckedChange: (TodoItem, Boolean) -> Unit,
    onSave: (List<TodoItem>, List<TodoItem>) -> Unit
) {
    // 상태 기억을 위한 remember (false로 자동 초기화 방지)
    var isEditMode by remember { mutableStateOf(false) }

    // 수정하기에서 사용
    var tempCommonList by remember { mutableStateOf(commonList) }
    var tempPersonalList by remember { mutableStateOf(personalList) }

    // ViewModel의 원본 데이터가 변경되면 임시 리스트도 업데이트
    LaunchedEffect(commonList) {
        tempCommonList = commonList
    }
    LaunchedEffect(personalList) {
        tempPersonalList = personalList
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(Modifier.fillMaxWidth()) {
                // 공동 리스트
                TodoListSection(
                    title = "공동 리스트",
                    items = if (isEditMode) tempCommonList else commonList,
                    onItemCheckedChange = { item ->
                        // 체크 변경은 즉시 ViewModel로 전달 (수정 모드에서는 체크 불가)
                        onItemCheckedChange(item, true)
                    },
                    onAddItem = { text ->
                        tempCommonList = tempCommonList + TodoItem(text, false)
                    },
                    onDeleteItem = { index ->
                        tempCommonList = tempCommonList.toMutableList().also { it.removeAt(index) }
                    },
                    isEditMode = isEditMode,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // 개인 리스트
                TodoListSection(
                    title = "개인 리스트",
                    items = if (isEditMode) tempPersonalList else personalList,
                    onItemCheckedChange = { item ->
                        onItemCheckedChange(item, false)
                    },
                    onAddItem = { text ->
                        tempPersonalList = tempPersonalList + TodoItem(text, false)
                    },
                    onDeleteItem = { index ->
                        tempPersonalList = tempPersonalList.toMutableList().also { it.removeAt(index) }
                    },
                    isEditMode = isEditMode,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                FilledTonalButton(
                    onClick = {
                        if (isEditMode) {
                            // '완료' 버튼 클릭 시, ViewModel의 saveLists 함수 호출
                            onSave(tempCommonList, tempPersonalList)
                        }
                        isEditMode = !isEditMode
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = if (isEditMode) {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        ButtonDefaults.filledTonalButtonColors()
                    }
                ) {
                    val icon = if (isEditMode) Icons.Default.Check else Icons.Default.Edit
                    val text = if (isEditMode) "완료" else "수정하기"
                    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text)
                }
            }
        }
    }
}

// TodoList 섹션
@Composable
fun TodoListSection(
    title: String,
    items: List<TodoItem>,
    onItemCheckedChange: (TodoItem) -> Unit,
    onAddItem: (String) -> Unit,
    onDeleteItem: (Int) -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier
) {
    val checkedColor = MaterialTheme.colorScheme.secondary
    val uncheckedColor = MaterialTheme.colorScheme.outline
    val cardBackgroundColor = MaterialTheme.colorScheme.primaryContainer
    var newTodoText by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(bottom = 16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // 수정 모드일 때만 '추가' UI 표시
            if (isEditMode) {
                if (isAdding) {
                    // 텍스트 입력 필드
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp)
                    ) {
                        BasicTextField(
                            value = newTodoText,
                            onValueChange = { newTodoText = it },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                        IconButton(onClick = {
                            if (newTodoText.isNotBlank()) {
                                onAddItem(newTodoText)
                                newTodoText = ""
                                isAdding = false
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "항목 추가 확인", tint = checkedColor)
                        }
                    }
                } else {
                    // '+' 버튼
                    IconButton(onClick = { isAdding = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Add, contentDescription = "항목 추가", tint = uncheckedColor)
                    }
                }
            }

            // 기존 항목 리스트
            items.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (!isEditMode) { // 수정 모드가 아닐 때만 체크 가능
                            // 인덱스가 아닌, 클릭된 item 객체 전달
                            onItemCheckedChange(item)
                        }
                    }
                ) {
                    if (item.checked) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Outlined.CheckBox, "Checked", tint = checkedColor, modifier = Modifier.size(24.dp))
                            Icon(Icons.Default.Check, null, tint = cardBackgroundColor, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Icon(Icons.Outlined.CheckBoxOutlineBlank, "Unchecked", tint = uncheckedColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item.text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

                    // 수정 모드일 때만 '삭제' 버튼 표시
                    if (isEditMode) {
                        IconButton(onClick = { onDeleteItem(index) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "삭제", tint = uncheckedColor)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "HomeScreen Preview")
@Composable
fun HomeScreenPreview() {
    KauplanHomeScreenTheme {
        HomeScreen(navController = rememberNavController())
    }
}