package com.example.kau_plan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- 색상 정의 ---
val MainPurple = Color(0xFF5B4DFF)
val DisabledGrey = Color(0xFFD9D9D9)
val TextBlack = Color(0xFF000000)
val BorderGrey = Color(0xFFE0E0E0)

// --- 데이터 모델 ---
data class ReservationItemData(
    val id: Int,
    val title: String,
    val iconVector: ImageVector? = null,
    val imageResId: Int? = null
)

// ★ 메인 화면
@Composable
fun ReservationScreen() {
    var selectedItemTitle by remember { mutableStateOf<String?>(null) }

    if (selectedItemTitle == null) {
        ReservationMenuScreen(
            onItemClick = { title ->
                selectedItemTitle = title
            }
        )
    } else {
        ReservationFormScreen(
            itemTitle = selectedItemTitle!!,
            onBack = { selectedItemTitle = null }
        )
    }
}

// --- 1. 메뉴 선택 화면 ---
@Composable
fun ReservationMenuScreen(onItemClick: (String) -> Unit) {
    // 모든 항목에 이미지 적용 (세탁기, 건조기, 냉장고)
    val items = listOf(
        ReservationItemData(1, "세탁기", imageResId = R.drawable.washingmachine),
        ReservationItemData(2, "건조기", imageResId = R.drawable.dryer),
        // 냉장고 관련 메뉴 2개 모두 mini.png 사용
        ReservationItemData(3, "냉장고 출입 신고", imageResId = R.drawable.mini),
        ReservationItemData(4, "냉장고 반출 신고", imageResId = R.drawable.mini)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderTitle(title = "공동 물품 사용 예약")

        HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("예약하실 품목")
                }
                append("을 선택해주세요")
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                ReservationCard(item = item, onClick = { onItemClick(item.title) })
            }
        }
    }
}

// --- 2. 입력 폼 화면 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationFormScreen(itemTitle: String, onBack: () -> Unit) {
    val isFridge = itemTitle.contains("냉장고")

    // --- 공통 상태 ---
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    // --- 세탁기/건조기 전용 상태 ---
    var selectedMachineOption by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("이용 가능한 시간대를 선택해주세요") }
    val timeOptions = listOf("09:00 ~ 10:00", "10:00 ~ 11:00", "11:00 ~ 12:00", "12:00 ~ 13:00")

    // --- 냉장고 전용 상태 ---
    var selectedReportType by remember {
        mutableStateOf(if (itemTitle.contains("출입")) "출입 신고" else "반출 신고")
    }
    var fridgeContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "공동 물품 사용 예약",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- 타이틀 ---
            if (isFridge) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                            append("냉장고 출입 / 반출 신고")
                        }
                        append(" 정보를\n입력해주세요")
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
            } else {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                            append("예약 정보")
                        }
                        append("를 입력해주세요")
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "* 표시는 필수 입력 사항입니다", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // --- 1. 이름 ---
            InputLabel(text = "이름", isRequired = true)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("신상원", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderGrey,
                    focusedBorderColor = MainPurple
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. 학번 ---
            InputLabel(text = "학번", isRequired = true)
            OutlinedTextField(
                value = studentId,
                onValueChange = { studentId = it },
                placeholder = { Text("2112344567", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderGrey,
                    focusedBorderColor = MainPurple
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3 & 4. 폼 내용 ---
            if (isFridge) {
                // 냉장고 폼
                InputLabel(text = "출입 / 반출 유형을 선택해주세요", isRequired = true)
                val reportOptions = listOf("출입 신고", "반출 신고")
                reportOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReportType = option }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedReportType == option),
                            onClick = { selectedReportType = option },
                            colors = RadioButtonDefaults.colors(selectedColor = MainPurple)
                        )
                        Text(text = option, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                InputLabel(text = "출입 / 반출 내용을 입력해주세요", isRequired = true)
                OutlinedTextField(
                    value = fridgeContent,
                    onValueChange = { fridgeContent = it },
                    placeholder = { Text("ex . 비비고 왕만두 (냉동) 2팩", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderGrey,
                        focusedBorderColor = MainPurple
                    ),
                    maxLines = 5
                )

            } else {
                // 세탁기/건조기 폼
                InputLabel(text = "예약하고자 하는 $itemTitle 종류를 선택해주세요", isRequired = true)
                val machineOptions = listOf("$itemTitle - A", "$itemTitle - B", "$itemTitle - C")
                machineOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMachineOption = option }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedMachineOption == option),
                            onClick = { selectedMachineOption = option },
                            colors = RadioButtonDefaults.colors(selectedColor = MainPurple)
                        )
                        Text(text = option, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                InputLabel(text = "이용하실려는 시간대를 선택해주세요", isRequired = true)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGrey, RoundedCornerShape(24.dp))
                        .clickable { expanded = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedTime, color = if(selectedTime.contains("선택")) Color.Gray else Color.Black)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        timeOptions.forEach { time ->
                            DropdownMenuItem(
                                text = { Text(time) },
                                onClick = {
                                    selectedTime = time
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- 확인 버튼 ---
            Button(
                onClick = { /* DB 전송 로직 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainPurple)
            ) {
                Text(text = "확인하기", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- 보조 컴포저블 ---

@Composable
fun HeaderTitle(title: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InputLabel(text: String, isRequired: Boolean) {
    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (isRequired) {
            Text(text = " *", color = MainPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun ReservationCard(item: ReservationItemData, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(DisabledGrey)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (item.imageResId != null) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.title,
                modifier = Modifier.size(64.dp)
            )
        } else if (item.iconVector != null) {
            Icon(
                imageVector = item.iconVector,
                contentDescription = item.title,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = item.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservation() {
    ReservationScreen()
}