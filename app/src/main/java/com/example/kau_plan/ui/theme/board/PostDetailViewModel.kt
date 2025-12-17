package com.example.kau_plan.ui.theme.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDetailViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    fun fetchPostAndComments(postId: String) {
        // 게시물 상세 정보 가져오기 (코루틴 사용)
        viewModelScope.launch {
            try {
                val document = db.collection("posts").document(postId).get().await()
                _post.value = document.toObject(Post::class.java)?.copy(id = document.id)
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Error getting post details", e)
            }
        }

        // 댓글 목록 실시간으로 가져오기
        db.collection("posts").document(postId).collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("PostDetailViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }
                _comments.value = snapshot?.documents?.mapNotNull {
                    it.toObject(Comment::class.java)?.copy(id = it.id)
                } ?: emptyList()
            }
    }

    fun addComment(postId: String, commentText: String) {
        if (commentText.isBlank()) return

        viewModelScope.launch {
            val commentData = hashMapOf(
                "content" to commentText,
                "authorName" to "익명", // 회원가입 미구현으로 인한 하드코딩
                "authorId" to "user123", // 회원가입 미구현으로 인한 하드코딩
                "createdAt" to FieldValue.serverTimestamp()
            )
            try {
                db.collection("posts").document(postId).collection("comments").add(commentData)
                    .await()
                // 댓글 수 업데이트
                db.collection("posts").document(postId)
                    .update("commentCount", FieldValue.increment(1)).await()
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Error adding comment", e)
            }
        }
    }
}