package com.example.kau_plan.ui.theme.board

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.items
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
import com.example.kau_plan.ui.theme.DetailCardBackground
import com.example.kau_plan.ui.theme.DetailSubTextColor
import com.example.kau_plan.ui.theme.navigation.BottomNavItem
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// 선택된 게시글의 상세 정보 표시
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailScreen(
    navController: NavController,
    postId: String,
    viewModel: PostDetailViewModel = viewModel()
) {
    // 게시글 상세 데이터 상태
    val post by viewModel.post.collectAsState()

    // 해당 게시글의 댓글 목록 상태
    val comments by viewModel.comments.collectAsState()

    // 댓글 입력창에 입력 중인 텍스트 상태
    var newCommentText by remember { mutableStateOf("") }

    // 게시글 상세 정보와 댓글 가져오기
    LaunchedEffect(postId) {
        if (postId.isNotBlank()) {
            viewModel.fetchPostAndComments(postId)
        }
    }

    Scaffold(
        topBar = { DetailTopAppBar(navController = navController) },
        bottomBar = {
            CommentInputField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                onSendClick = {
                    viewModel.addComment(postId, newCommentText)
                    newCommentText = ""
                }
            )
        },
        containerColor = BoardBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 게시글 본문
            item {
                post?.let { PostContent(post = it) }
            }
            // 댓글 개수
            item {
                Text(
                    "댓글 ${comments.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            // 댓글 목록
            items(comments, key = { it.id }) { comment ->
                CommentItem(comment = comment)
            }
        }
    }
}

// 게시글 상세 화면 상단 앱바
@Composable
fun DetailTopAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = "뒤로가기",
                tint = Color.DarkGray
            )
        }

        Text(
            "게시판",
            color = Color.DarkGray,
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
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "프로필",
                    tint = Color.DarkGray
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = Color.DarkGray
                )
            }
        }
    }
}

// 게시글 본문
@Composable
fun PostContent(post: Post) {
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
            Text(
                post.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                post.createdAt?.toFormattedString() ?: "",
                fontSize = 12.sp,
                color = DetailSubTextColor
            )
        }
        // 모집 상태 뱃지
        StatusBadge(status = post.status)
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
        // 게시글 이미지 (있을 경우에만 표시)
        post.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = "게시물 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Text(post.content, lineHeight = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        // 작성자 정보
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
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = "작성자 프로필",
                    tint = Color.Gray
                )
            }
            Text(post.authorName, fontWeight = FontWeight.Bold)
        }
    }
}

// 댓글 표시
@Composable
fun CommentItem(comment: Comment) {
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
            // 댓글 작성자 정보
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
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "프로필",
                        tint = Color.Gray
                    )
                }
                Text(comment.authorName, fontWeight = FontWeight.Bold)
            }
            // 댓글 작성 시각
            Text(
                comment.createdAt?.toFormattedString() ?: "",
                fontSize = 12.sp,
                color = DetailSubTextColor
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // 댓글 내용
        Text(
            comment.content,
            modifier = Modifier.padding(start = 52.dp)
        )
    }
}

// 댓글 입력 필드
@Composable
fun CommentInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BoardBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("댓글을 입력해주세요.", color = DetailSubTextColor)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            trailingIcon = {
                IconButton(
                    onClick = onSendClick,
                    enabled = value.isNotBlank()
                ) {
                    Icon(
                        Icons.Outlined.Send,
                        contentDescription = "전송",
                        tint = if (value.isNotBlank()) BoardPrimary else Color.Gray
                    )
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

fun Timestamp.toFormattedString(): String {
    val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return sdf.format(this.toDate())
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PostDetailScreenPreview() {
    PostDetailScreen(
        navController = rememberNavController(),
        postId = ""
    )
}
