package com.example.kau_plan.ui.theme.board

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * 게시글 모집 상태를 나타내는 열거형
 * - RECRUITING: 모집 중인 상태
 * - COMPLETED: 모집이 완료된 상태
 * 게시글 카드 및 상태 뱃지 UI에서 사용된다.
 */
enum class PostStatus {
    RECRUITING,
    COMPLETED
}

/**
 * 게시글(Post) 데이터를 표현하는 모델 클래스
 * Firestore의 posts 컬렉션과 1:1로 매핑되며,
 * 게시판 목록, 상세 화면 등 전반적인 게시글 UI에서 사용된다.
 */
data class Post(

    /**
     * Firestore 문서 ID
     * 문서 생성 시 자동으로 부여되며, 조회 후 수동으로 주입된다.
     */
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val status: PostStatus = PostStatus.RECRUITING,
    val authorId: String = "",
    val authorName: String = "",

    /**
     * 게시글 생성 시각
     * @ServerTimestamp 어노테이션을 통해
     * Firestore 서버 기준 시간으로 자동 설정된다.
     */
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val imageUrl: String? = null,
    val commentCount: Int = 0
)

/**
 * 게시글에 달리는 댓글 데이터를 표현하는 모델 클래스
 * 게시글 상세 화면에서 댓글 목록을 구성하는 데 사용된다.
 */
data class Comment(

    // 댓글 문서 ID
    val id: String = "",

    // 댓글 본문 내용
    val content: String = "",

    // 댓글 작성자의 표시 이름
    val authorName: String = "",

    // 댓글 작성자의 고유 ID
    val authorId: String = "",

    // 댓글 작성 시각
    // Firestore 서버 시간 기준으로 자동 설정
    @ServerTimestamp
    val createdAt: Timestamp? = null
)
