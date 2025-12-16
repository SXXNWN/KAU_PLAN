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
    // ViewModel의 posts 상태를 구독합니다. posts가 변경되면 화면이 자동으로 다시 그려집니다.
    // `collectAsState`는 Flow를 Composable이 관찰할 수 있는 State 객체로 변환합니다.
    val posts by boardViewModel.posts.collectAsState()

    // ViewModel의 로딩 상태를 구독합니다. 데이터 로딩 중에 로딩 인디케이터를 표시하는 데 사용됩니다.
    val isLoading by boardViewModel.isLoading.collectAsState()

    val categories = listOf("전체", "헬스", "식사", "공부", "기타")

    // 현재 선택된 카테고리를 저장하는 상태 변수입니다.
    // `remember`는 Composable이 리컴포지션 되어도 상태를 유지시켜 줍니다.
    // `mutableStateOf`는 변경 가능한 상태를 만듭니다.
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    /**
     * `LaunchedEffect`는 Composable의 생명주기 내에서 코루틴을 실행하는 데 사용됩니다.
     * `key1`으로 지정된 `selectedCategory`가 변경될 때마다 중괄호 안의 코드가 다시 실행됩니다.
     * 이를 통해 사용자가 카테고리를 변경할 때마다 새로운 게시글 목록을 불러옵니다.
     */
    LaunchedEffect(selectedCategory) {
        boardViewModel.fetchPosts(selectedCategory)
    }

    // `Scaffold`는 머티리얼 디자인의 기본 레이아웃 구조(상단바, 하단바, FAB 등)를 제공합니다.
    Scaffold(
        // FloatingActionButton: 화면 위에 떠 있는 버튼으로, 주로 주요 액션을 위해 사용됩니다.
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("글쓰기") },
                icon = { Icon(Icons.Outlined.Edit, contentDescription = "글쓰기") },
                onClick = {
                    // 클릭 시 'write_post' 라우트로 이동합니다.
                    navController.navigate("write_post")
                },
                containerColor = Color.White,
                contentColor = Color.DarkGray
            )
        },
        // 화면 전체의 기본 배경색을 지정합니다.
        containerColor = BoardBackground
    ) { innerPadding -> // Scaffold가 시스템 UI(상태바 등)를 위해 예약한 공간 정보입니다.
        // 화면의 주요 콘텐츠를 담는 컨테이너입니다.
        Column(modifier = Modifier.padding(innerPadding)) {

            Spacer(modifier = Modifier.height(8.dp))

            // 게시판 상단 바 (제목, 프로필, 설정 버튼)를 표시합니다.
            BoardTopAppBar(navController = navController)

            Spacer(modifier = Modifier.height(12.dp))

            // 카테고리 선택 칩 UI를 표시합니다.
            CategoryChips(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it } // 칩 선택 시 상태를 업데이트합니다.
            )

            Spacer(modifier = Modifier.height(12.dp))

            // `isLoading` 상태에 따라 분기 처리하여 다른 UI를 보여줍니다.
            if (isLoading) {
                // 로딩 중일 때 화면 중앙에 원형 프로그레스 바를 표시합니다.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (posts.isEmpty()) {
                // 로딩이 끝났지만 게시글이 없을 때 안내 문구를 표시합니다.
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
                // 게시글이 있을 경우 `LazyColumn`을 사용해 목록을 효율적으로 표시합니다.
                // `LazyColumn`은 화면에 보이는 항목만 렌더링하여 성능을 최적화합니다.
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // 아이템 사이의 수직 간격을 지정합니다.
                ) {
                    // `items` 함수는 리스트의 각 항목에 대해 Composable을 생성합니다.
                    // `key`를 지정하면 리스트가 변경될 때 효율적인 리컴포지션이 가능합니다.
                    items(posts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onClick = {
                                // 카드를 클릭하면 해당 게시물의 id를 포함한 상세 페이지로 이동합니다.
                                navController.navigate("post_detail/${post.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 게시판 화면의 상단에 위치하는 앱 바(AppBar)입니다.
 * 화면 제목과 사용자 관련 아이콘 버튼들을 포함합니다.
 *
 * @param navController 프로필 화면 등으로 이동하기 위해 사용됩니다.
 */
@Composable
fun BoardTopAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // 좌우 여백
            .clip(RoundedCornerShape(50)) // 둥근 모서리 적용
            .background(BoardPrimary) // 배경색 지정
            .padding(horizontal = 20.dp, vertical = 12.dp), // 내부 여백
        horizontalArrangement = Arrangement.SpaceBetween, // 자식 요소들을 양 끝으로 정렬
        verticalAlignment = Alignment.CenterVertically // 자식 요소들을 수직 중앙 정렬
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
                    // 프로필 아이콘 클릭 시 프로필 화면으로 이동합니다.
                    navController.navigate(BottomNavItem.Profile.route) {
                        // 네비게이션 스택 관리를 위한 옵션들입니다.
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
                onClick = { /* 현재는 기능 없음 */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Settings, "설정", tint = Color.White)
            }
        }
    }
}

/**
 * 카테고리 필터링을 위한 버튼 그룹(칩)입니다.
 *
 * @param categories 표시할 카테고리 이름 목록입니다.
 * @param selectedCategory 현재 선택된 카테고리 이름입니다.
 * @param onCategorySelected 카테고리가 선택되었을 때 호출될 콜백 함수입니다.
 */
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
                    // 선택된 카테고리와 현재 카테고리가 같으면 활성화 색상, 다르면 비활성화 색상을 적용합니다.
                    containerColor = if (selectedCategory == category) BoardPrimary else Color.LightGray,
                    contentColor = if (selectedCategory == category) Color.White else Color.DarkGray
                ),
                shape = CircleShape, // 원형 버튼 모양
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.weight(1f) // 모든 버튼이 동일한 너비를 갖도록 합니다.
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

/**
 * 게시글 하나를 나타내는 카드 UI입니다.
 *
 * @param post 표시할 게시물 데이터 객체입니다.
 * @param onClick 카드가 클릭되었을 때 실행될 함수입니다.
 */
@Composable
fun PostCard(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // 카드 전체에 클릭 이벤트를 적용합니다.
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PostCardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                // 프로필 아이콘, 제목, 시간 등 좌측 컨텐츠 영역
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f) // 남은 공간을 모두 차지하도록 설정
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
                                maxLines = 1, // 제목이 길 경우 한 줄로 제한
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
                modifier = Modifier.padding(start = 52.dp), // 프로필 영역만큼 들여쓰기
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 게시글에 이미지가 있을 경우에만 이미지 뷰를 표시합니다.
            post.imageUrl?.let {
                Spacer(modifier = Modifier.height(12.dp))
                // `AsyncImage`는 Coil 라이브러리의 Composable로, URL로부터 이미지를 비동기적으로 로드합니다.
                AsyncImage(
                    model = it,
                    contentDescription = "게시물 이미지",
                    modifier = Modifier
                        .padding(start = 52.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop // 이미지가 영역을 꽉 채우도록 비율을 조절합니다.
                )
            }
        }
    }
}

/**
 * 게시글의 모집 상태("모집중", "모집완료")를 시각적으로 보여주는 뱃지입니다.
 *
 * @param status 표시할 모집 상태 Enum 값입니다.
 */
@Composable
fun StatusBadge(status: PostStatus) {
    // `when` 표현식을 사용하여 상태에 따라 다른 텍스트와 색상을 지정합니다.
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

/**
 * Firebase의 `Timestamp` 객체를 현재 시간과 비교하여 상대적인 시간 문자열
 * (예: "방금 전", "5분 전", "2시간 전", "3일 전")로 변환하는 확장 함수입니다.
 *
 * @return 변환된 상대 시간 문자열입니다.
 */
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

/**
 * Android Studio의 디자인 탭에서 `BoardScreen`의 UI를 미리 볼 수 있게 해주는 Composable입니다.
 * 실제 앱 빌드에는 포함되지 않습니다.
 */
@Preview(showBackground = true)
@Composable
fun BoardScreenPreview() {
    BoardScreen(navController = rememberNavController())
}