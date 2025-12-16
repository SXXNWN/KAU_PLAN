package com.example.kau_plan.ui.theme.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.kau_plan.ui.theme.DetailCardBackground
import com.example.kau_plan.ui.theme.DetailSubTextColor
import com.example.kau_plan.ui.theme.BoardBackground
import com.example.kau_plan.ui.theme.BoardPrimary


@Composable
fun PostDetailScreen(navController: NavController) {
    Scaffold(
        // topBar를 제거하고 LazyColumn 안으로 이동
        bottomBar = { CommentInputField() },
        containerColor = BoardBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            // BoardScreen과 동일하게 horizontal만 설정
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 헤더를 첫 번째 아이템으로 배치하고 위쪽에 여백 추가
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DetailTopAppBar(navController)
            }
            // 게시물 본문
            item {
                PostContent()
            }
            // 댓글 작성자 정보
            item {
                CommenterProfile()
            }
            // 댓글 목록
            item {
                Comment()
            }
        }
    }
}

@Composable
fun DetailTopAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // BoardScreen의 헤더와 동일한 모양과 패딩 적용
            .clip(CircleShape)
            .background(BoardPrimary)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) { // 뒤로가기 기능
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "뒤로가기", tint = Color.White)
        }
        Text("게시판", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { navController.navigate("profile") {
                navController.navigate("profile") {
                    // 다른 탭으로 이동하는 것과 동일한 옵션 적용
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            } }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "프로필",
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* ... */ }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun PostContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DetailCardBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text("같이 헬스 할 사람~", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("11/07 21:27", fontSize = 12.sp, color = DetailSubTextColor)
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF6EE7B7)) // green-300
                .padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(
                "모집중",
                color = Color(0xFF047857),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            ) // green-800
        }
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("오늘 등이랑 하체 할건데 같이 할 사람 구합니당!", lineHeight = 24.sp)
            Text("이따 6시 반쯤 학관 헬스장에서 운동할거에요", lineHeight = 24.sp)
            Text("같이 하실 분 편하게 댓글 남겨주세요!", lineHeight = 24.sp)
        }
    }
}

@Composable
fun CommenterProfile() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DetailCardBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = "프로필", tint = Color.Gray)
            }
            Column {
                Text("정윤님", fontWeight = FontWeight.Bold)
                Text("가장 많이 사용한 태그", fontSize = 12.sp, color = DetailSubTextColor)
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("헬스", fontSize = 12.sp, color = DetailSubTextColor)
        }
    }
}

@Composable
fun Comment() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DetailCardBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = "프로필", tint = Color.Gray)
                }
                Text("상원님", fontWeight = FontWeight.Bold)
            }
            Text("11/07 21:27", fontSize = 12.sp, color = DetailSubTextColor)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("저요저요!!!", modifier = Modifier.padding(start = 52.dp))
    }
}

@Composable
fun CommentInputField() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BoardBackground) // 배경색을 화면과 동일하게 맞춤
            .padding(horizontal = 16.dp, vertical = 8.dp) // 여백 조정
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("댓글을 입력해주세요.", color = DetailSubTextColor) },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            trailingIcon = {
                IconButton(onClick = { /* 댓글 전송 */ }) {
                    Icon(Icons.Outlined.Send, contentDescription = "전송", tint = BoardPrimary)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = DetailCardBackground,
                focusedContainerColor = DetailCardBackground,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = BoardPrimary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PostDetailScreenPreview() {
    PostDetailScreen(navController = rememberNavController())
}