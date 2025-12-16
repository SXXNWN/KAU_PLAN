package com.example.kau_plan.ui.theme.board

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kau_plan.R // 이미지 리소스를 위해 R 클래스를 import 합니다.
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.kau_plan.ui.theme.BoardBackground
import com.example.kau_plan.ui.theme.BoardPrimary

// 게시물 데이터 모델 정의
data class Post(
    val id: Int,
    val author: String,
    val title: String,
    val content: String,
    val timeAgo: String,
    val commentCount: Int,
    val status: PostStatus,
    val imageUrl: Int? = null // 이미지 리소스 ID (nullable)
)

// 게시물 모집 상태 (모집중, 모집완료)
enum class PostStatus(val displayName: String, val color: Color) {
    RECRUITING("모집중", Color(0xFF10B981)), // green-500
    COMPLETED("모집완료", Color(0xFF6B7280)) // gray-500
}

// 샘플 게시물 데이터
val samplePosts = listOf(
    Post(1, "익명1", "같이 헬스 할 사람~", "오늘 등이랑 하체 할건데 같이 할...", "5분 전", 1, PostStatus.RECRUITING),
    Post(2, "익명2", "저녁 같이 드실 분", "학식 말고 맛있는거 먹고 싶어요!", "12분 전", 4, PostStatus.RECRUITING, R.drawable.ic_launcher_background), // drawable에 ramen.jpg 추가 필요
    Post(3, "익명3", "같이 헬스 할 사람~", "오늘 등이랑 하체 할건데 같이 할...", "1시간 전", 3, PostStatus.COMPLETED),
    Post(4, "익명4", "스터디 같이 하실분?", "중앙도서관에서 같이 공부해요.", "2시간 전", 2, PostStatus.COMPLETED),
)

val PostCardBackground = BoardPrimary.copy(alpha = 0.2f)

@Composable
fun BoardScreen(navController: NavController) {
    // 게시물 목록 상태 관리
    var posts by remember { mutableStateOf(samplePosts) }

    Scaffold(
        // topBar를 제거하고 LazyColumn 안으로 이동
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("글쓰기") },
                icon = { Icon(Icons.Outlined.Edit, contentDescription = "글쓰기") },
                onClick = {
                    // 새 게시물 추가
                    navController.navigate("writePost")
                },
                containerColor = Color.White,
                contentColor = Color.DarkGray
            )
        },
        containerColor = BoardBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp), // vertical padding은 각 item에서 관리
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. 헤더를 LazyColumn의 첫 번째 아이템으로 배치하고 위쪽에 여백 추가
            item {
                Spacer(modifier = Modifier.height(8.dp)) // 헤더 위쪽 여백
                BoardTopAppBar(navController = navController)
            }

            // 2. 카테고리 필터
            item {
                CategoryChips()
            }

            // 게시물 목록
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    onClick = {
                        // 클릭 시 "postDetail" 경로로 이동
                        navController.navigate("postDetail")
                    }
                )
            }
        }
    }
}

@Composable
fun BoardTopAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(BoardPrimary)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("게시판", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
fun CategoryChips() {
    val categories = listOf("전체", "헬스", "식사", "공부", "기타")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            Button(
                onClick = { selectedCategory = category },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCategory == category) BoardPrimary else Color.LightGray,
                    contentColor = if (selectedCategory == category) Color.White else Color.DarkGray
                ),
                shape = CircleShape,
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.weight(1f) // 모든 버튼이 동일한 너비를 갖도록 설정
            ) {
                Text(
                    text = category,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PostCard(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PostCardBackground),
        onClick = onClick // 파라미터로 받은 onClick 람다를 사용
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 상단 정보 (프로필, 제목, 댓글)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = "프로필", tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(status = post.status)
                        }
                        Text(post.timeAgo, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "댓글", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(post.commentCount.toString(), fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 본문 내용
            Text(
                text = post.content,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 52.dp) // 프로필 사진 너비 + 간격
            )

            // 이미지 (있을 경우)
            post.imageUrl?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "게시물 이미지",
                    modifier = Modifier
                        .padding(start = 52.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: PostStatus) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(status.color)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(status.displayName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun BoardScreenPreview() {
    BoardScreen(navController = rememberNavController())
}