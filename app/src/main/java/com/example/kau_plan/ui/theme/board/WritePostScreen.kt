package com.example.kau_plan.ui.theme.board

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kau_plan.ui.theme.BoardBackground
import com.example.kau_plan.ui.theme.BoardPrimary
import com.example.kau_plan.ui.theme.board.PostStatus
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

// 테두리 색상을 일관되게 관리하기 위해 변수 추가
val inputBorderColor = Color.LightGray.copy(alpha = 0.7f)

@Composable
fun WritePostScreen(navController: NavController) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }
    val categories = listOf("헬스", "식사", "공부", "기타")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var selectedStatus by remember { mutableStateOf(PostStatus.RECRUITING) } // 모집 상태 추가
    var isUploading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { WritePostTopAppBar(navController) }, // NavController 전달
        containerColor = BoardBackground
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                item {
                    InputSection(
                        label = "제목",
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "제목을 입력해주세요."
                    )
                }
                item {
                    CategorySelector(
                        selectedCategory = selectedCategory,
                        categories = categories,
                        onCategorySelected = { selectedCategory = it }
                    )
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("모집 상태: ", fontWeight = FontWeight.Bold)
                        Row {
                            RadioButton(selected = selectedStatus == PostStatus.RECRUITING, onClick = { selectedStatus = PostStatus.RECRUITING })
                            Text("모집중", modifier = Modifier.align(Alignment.CenterVertically))
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(selected = selectedStatus == PostStatus.COMPLETED, onClick = { selectedStatus = PostStatus.COMPLETED })
                            Text("모집완료", modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }
                item {
                    InputSection(
                        label = "내용",
                        value = content,
                        onValueChange = { content = it },
                        placeholder = "내용을 입력해주세요.",
                        singleLine = false,
                        rows = 8
                    )
                }
                item {
                    ImageUploader() // 이미지 관련 파라미터 제거
                }
                item {
                    Button(
                        onClick = {
                            if (title.text.isNotBlank() && content.text.isNotBlank() && !isUploading) {
                                isUploading = true
                                val newPost = hashMapOf(
                                    "title" to title.text,
                                    "content" to content.text,
                                    "category" to selectedCategory,
                                    "status" to selectedStatus.name, // enum의 이름을 String으로 저장
                                    "authorId" to "user123", // TODO: 실제 사용자 ID로 교체
                                    "authorName" to "익명", // TODO: 실제 사용자 이름으로 교체
                                    "createdAt" to FieldValue.serverTimestamp(),
                                    "imageUrl" to null,
                                    "commentCount" to 0
                                )
                                Firebase.firestore.collection("posts").add(newPost)
                                    .addOnSuccessListener {
                                        isUploading = false
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        isUploading = false
                                        Log.w("WritePostScreen", "Error writing post", e)
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BoardPrimary)
                    ) {
                        Text("글 등록하기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WritePostTopAppBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(BoardPrimary)
            .padding(vertical = 8.dp, horizontal = 4.dp), // 패딩 조정
        contentAlignment = Alignment.Center
    ) {
        // 뒤로가기 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "뒤로가기",
                    tint = Color.White
                )
            }
        }
        Text("글쓰기", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InputSection(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    rows: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (singleLine) 56.dp else (56 * (rows / 2)).dp)
                // 테두리 추가
                .border(1.dp, inputBorderColor, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = singleLine
        )
        if (label == "내용") {
            Text(
                "타인에 대한 비방, 불법적 내용, 도덕적으로 문제가 되는 내용 등을 담은 글은 삭제될 수 있습니다.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CategorySelector(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("카테고리", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                // 테두리 색상 적용
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(inputBorderColor))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedCategory, color = Color.Black)
                    Icon(Icons.Default.ExpandMore, contentDescription = "카테고리 선택", tint = Color.Gray)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ImageUploader() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("이미지 추가", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .clip(RoundedCornerShape(12.dp))
                // 테두리 색상 적용
                .border(
                    width = 1.dp, // 테두리 두께 통일
                    color = inputBorderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(Color.White) // 배경색 추가
                .clickable { /* 이미지 선택 로직 (비워둠) */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = "이미지 추가",
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
                Text("이미지를 추가하려면 탭하세요", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WritePostScreenPreview() {
    WritePostScreen(navController = rememberNavController())
}