package com.example.kau_plan.ui.theme.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// 게시판 ViewModel
class BoardViewModel : ViewModel() {

    // Firebase 접근
    private val db = Firebase.firestore

    // 게시글 Firebase 컬랙션 참조
    private val postsCollection = db.collection("posts")

    /* 게시글 목록 상태
     내부에서는 MutableStateFlow로 값을 변경
     외부(UI)에는 읽기 전용 StateFlow로 노출 */
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    // 게시글 로딩 여부 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchPosts("전체")
    }

    // 게시글 조회
    fun fetchPosts(category: String) {
        viewModelScope.launch {

            // 네트워크 요청 시작 시 로딩 상태 활성화
            _isLoading.value = true

            try {
                /* 카테고리에 따라 Firestore 쿼리를 분기
                전체: 생성 시간 기준 내림차순 정렬
                특정 카테고리: category 필드로 필터링 */
                val query = if (category == "전체") {
                    postsCollection
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                } else {
                    postsCollection
                        .whereEqualTo("category", category)
                }

                // Firestore 쿼리 실행 및 결과 대기
                val snapshot = query.get().await()

                // Firestore Document를 Post 객체로 변환 - 문서 ID는 별도로 Post 모델에 주입
                _posts.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }

            } catch (e: Exception) {
                Log.e("BoardViewModel", "Error fetching posts", e)
            } finally {
                // 요청 성공/실패 여부와 관계없이 로딩 상태 해제
                _isLoading.value = false
            }
        }
    }
}
