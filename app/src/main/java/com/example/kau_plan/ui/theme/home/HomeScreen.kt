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

data class TodoItem(val text: String, val isChecked: Boolean)

@Composable
fun HomeScreen(navController: NavHostController) {
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
                DailyRoutineCard()
                TodoListCard()
            }
        }
    }
}

/**
 * 화면 상단의 날짜와 아이콘 버튼이 있는 헤더입니다.
 */
@Composable
fun HomeHeader(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)) // 원본 UI의 'rounded-full' 스타일에 맞게 수정
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Nov 7, 2025",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { navController.navigate("profile") {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            } }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "프로필",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer // onPrimary -> onPrimaryContainer로 수정
                )
            }
            IconButton(onClick = { /* ... */ }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer // onPrimary -> onPrimaryContainer로 수정
                )
            }
        }
    }
}

/**
 * 사용자 프로필과 생활비 현황을 보여주는 카드입니다.
 */
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
                    // 부제목 색상을 onSurfaceVariant로 변경
                    Text("35% 사용", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("잘 절약하는 중이네요! 😊", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


/**
 * 하루 루틴 달성률을 원형 프로그레스 바로 보여주는 카드입니다.
 */
@Composable
fun DailyRoutineCard() {
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
                CircularProgressBar(percentage = 0.3f, radius = 48.dp)
                Column {
                    Text("오후 8시까지 30% 달성!", style = MaterialTheme.typography.titleMedium)
                    // 부제목 색상을 onSurfaceVariant로 변경
                    Text("아직 할 일이 많아요! 😢", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

/**
 * 원형 프로그레스 바를 그리는 Composable
 */
@Composable
fun CircularProgressBar(percentage: Float, radius: Dp) {
    // ★ 1. 가로 프로그레스 바와 동일한 트랙 색상 사용
    val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    val progressColor = MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 배경 트랙 그리기 (연보라색)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 24f) // 원본 UI와 유사하게 두께 조정
            )
            // 진행률 표시 그리기 (노란색)
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


/**
 * 공동/개인 리스트를 보여주는 카드입니다.
 */
@Composable
fun TodoListCard() {
    var isEditMode by remember { mutableStateOf(false) }
    var commonList by remember {
        mutableStateOf(listOf(
            TodoItem("빨래하기", true),
            TodoItem("분리수거하기", false),
            TodoItem("청소하기", false)
        ))
    }
    var personalList by remember {
        mutableStateOf(listOf(
            TodoItem("운동하기", true),
            TodoItem("과제하기", false),
            TodoItem("독서하기", false)
        ))
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
                TodoListSection(
                    title = "공동 리스트",
                    items = commonList,
                    onItemCheckedChange = { index, isChecked ->
                        commonList = commonList.toMutableList().also { it[index] = it[index].copy(isChecked = isChecked) }
                    },
                    onAddItem = { text ->
                        commonList = commonList + TodoItem(text, false)
                    },
                    onDeleteItem = { index ->
                        commonList = commonList.toMutableList().also { it.removeAt(index) }
                    },
                    isEditMode = isEditMode,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                TodoListSection(
                    title = "개인 리스트",
                    items = personalList,
                    onItemCheckedChange = { index, isChecked ->
                        personalList = personalList.toMutableList().also { it[index] = it[index].copy(isChecked = isChecked) }
                    },
                    onAddItem = { text ->
                        personalList = personalList + TodoItem(text, false)
                    },
                    onDeleteItem = { index ->
                        personalList = personalList.toMutableList().also { it.removeAt(index) }
                    },
                    isEditMode = isEditMode,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                FilledTonalButton(
                    onClick = { isEditMode = !isEditMode },
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

/**
 * 하나의 TodoList 섹션을 그립니다.
 */
@Composable
fun TodoListSection(
    title: String,
    items: List<TodoItem>,
    onItemCheckedChange: (Int, Boolean) -> Unit,
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
                        modifier = Modifier.border(
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
                            onItemCheckedChange(index, !item.isChecked)
                        }
                    }
                ) {
                    if (item.isChecked) {
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


@Preview(showBackground = true, name = "HomeScreen Preview")
@Composable
fun HomeScreenPreview() {
    KauplanHomeScreenTheme {
        HomeScreen(navController = rememberNavController())
    }
}