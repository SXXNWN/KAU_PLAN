package com.example.kau_plan.ui.theme.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.kau_plan.ui.theme.BoardBackground
import com.example.kau_plan.ui.theme.BoardPrimary
import com.example.kau_plan.ui.theme.navigation.BottomNavItem
import com.google.firebase.Timestamp
import java.util.Date
import kotlin.math.abs

val PostCardBackground = BoardPrimary.copy(alpha = 0.2f)

// 화면 전체 구조(Scaffold), 데이터 상태 관리, UI 렌더링
@Composable
fun BoardScreen(
    navController: NavController,
    boardViewModel: BoardViewModel = viewModel()
) {
    val posts by boardViewModel.posts.collectAsState()
    val isLoading by boardViewModel.isLoading.collectAsState()
    val categories = listOf("전체", "헬스", "식사", "공부", "기타")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    // 카테고리 선택할 때마다 게시글 목록 새롭게 불러옴 (필터링)
    LaunchedEffect(selectedCategory) {
        boardViewModel.fetchPosts(selectedCategory)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("글쓰기") },
                icon = { Icon(Icons.Outlined.Edit, contentDescription = "글쓰기") },
                onClick = {
                    navController.navigate("write_post")
                },
                containerColor = Color.White,
                contentColor = Color.DarkGray
            )
        },
        containerColor = BoardBackground
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Spacer(modifier = Modifier.height(8.dp))
            BoardTopAppBar(navController = navController)
            Spacer(modifier = Modifier.height(12.dp))
            CategoryChips(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it } // 칩 선택 시 상태 업데이트
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                // 로딩 중 - 원형 프로그레스 바
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (posts.isEmpty()) {
                // 로딩 끝, 게시글 없을 때 - 안내 문구
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "아직 게시물이 없어요. 😢",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // 게시글이 있을 경우 화면에 보이는 항목만 렌더링
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onClick = {
                                navController.navigate("post_detail/${post.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// 게시판 화면의 상단 앱 바
@Composable
fun BoardTopAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(50))
            .background(BoardPrimary)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "게시판",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = {
                    navController.navigate(BottomNavItem.Profile.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.AccountCircle, "프로필", tint = Color.White)
            }

            IconButton(
                onClick = {  },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Settings, "설정", tint = Color.White)
            }
        }
    }
}

// 카테고리 필터링
@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            Button(
                onClick = { onCategorySelected(category) },
                colors = ButtonDefaults.buttonColors(
                    // 선택된 카테고리와 현재 카테고리가 같으면 활성화, 다르면 비활성화
                    containerColor = if (selectedCategory == category) BoardPrimary else Color.LightGray,
                    contentColor = if (selectedCategory == category) Color.White else Color.DarkGray
                ),
                shape = CircleShape,
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.weight(1f) // 모든 버튼 동일한 너비 적용
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

// 각 게시글
@Composable
fun PostCard(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PostCardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "프로필",
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1, // 제목 길이 한 줄로 제한
                                overflow = TextOverflow.Ellipsis // 넘어가는 텍스트는 ...으로 표시
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(status = post.status)
                        }

                        Text(
                            text = post.createdAt?.toRelativeTimeString() ?: "방금 전",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // 댓글 수 표시 영역
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "댓글",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = post.commentCount.toString(),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 게시글 내용 미리보기
            Text(
                text = post.content,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 52.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 게시글에 이미지가 있을 경우에만 이미지 뷰 표시
            post.imageUrl?.let {
                Spacer(modifier = Modifier.height(12.dp))
                // Coil 라이브러리의 AsyncImage Composable를 사용하여 URL로부터 이미지를 비동기적으로 로드
                AsyncImage(
                    model = it,
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

// 게시글의 모집 상태
@Composable
fun StatusBadge(status: PostStatus) {
    val (displayName, color) = when (status) {
        PostStatus.RECRUITING -> "모집중" to Color(0xFF10B981)
        PostStatus.COMPLETED -> "모집완료" to Color(0xFF6B7280)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = displayName,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Firebase의 Timestamp 객체를 현재 시간과 비교하여 상대적인 시간 문자열 표시
fun Timestamp.toRelativeTimeString(): String {
    val now = Date().time
    val diff = abs(now - this.toDate().time) // 현재 시간과 게시글 시간의 차이(절대값)

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}일 전"
        hours > 0 -> "${hours}시간 전"
        minutes > 0 -> "${minutes}분 전"
        else -> "방금 전"
    }
}

@Preview(showBackground = true)
@Composable
fun BoardScreenPreview() {
    BoardScreen(navController = rememberNavController())
}