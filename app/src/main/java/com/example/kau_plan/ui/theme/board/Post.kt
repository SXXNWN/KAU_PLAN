package com.example.kau_plan.ui.theme.board

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

enum class PostStatus {
    RECRUITING, // 모집 중
    COMPLETED // 모집 완료
}

// 게시글 데이터
data class Post(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val status: PostStatus = PostStatus.RECRUITING,
    val authorId: String = "",
    val authorName: String = "",

    @ServerTimestamp // Firestore 서버 시간 기준으로 자동 설정
    val createdAt: Timestamp? = null,
    val imageUrl: String? = null,
    val commentCount: Int = 0
)

// 게시글 댓글 데이터
data class Comment(
    val id: String = "",
    val content: String = "",
    val authorName: String = "",
    val authorId: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null
)
