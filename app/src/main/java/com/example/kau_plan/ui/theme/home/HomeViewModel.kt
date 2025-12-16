package com.example.kau_plan.ui.theme.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.get
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TodoItem(
    val text: String = "", // Firestore 매핑을 위해 기본값 추가
    val checked: Boolean = false
)

class HomeViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val commonListDocRef = db.collection("todo_lists").document("common_list")
    private val personalListDocRef = db.collection("todo_lists").document("personal_list")

    // UI가 관찰할 상태 변수들 (StateFlow 사용)
    private val _commonList = MutableStateFlow<List<TodoItem>>(emptyList())
    val commonList = _commonList.asStateFlow()

    private val _personalList = MutableStateFlow<List<TodoItem>>(emptyList())
    val personalList = _personalList.asStateFlow()

    // 공동 리스트와 개인 리스트를 합쳐서 전체 진행률 계산
    val dailyRoutineProgress = combine(_commonList, _personalList) { common, personal ->
        val totalList = common + personal
        if (totalList.isEmpty()) {
            0f // 리스트가 비어있으면 0%
        } else {
            val checkedCount = totalList.count { it.checked }
            checkedCount.toFloat() / totalList.size.toFloat() // 완료된 항목 비율 계산
        }
    }

    init {
        // ViewModel이 생성될 때 Firestore로부터 실시간으로 데이터 업데이트를 받기 시작
        fetchRealtimeUpdates()
    }

    private fun fetchRealtimeUpdates() {
        // 공동 리스트 실시간 업데이트
        commonListDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("HomeViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val itemsMap = snapshot.get("items") as? List<Map<String, Any>>
                _commonList.value =
                    itemsMap?.map { TodoItem(it["text"] as String, it["checked"] as Boolean) }
                        ?: emptyList()
            } else {
                Log.d("HomeViewModel", "Current data: null")
            }
        }

        // 개인 리스트 실시간 업데이트
        personalListDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("HomeViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val itemsMap = snapshot.get("items") as? List<Map<String, Any>>
                _personalList.value =
                    itemsMap?.map { TodoItem(it["text"] as String, it["checked"] as Boolean) }
                        ?: emptyList()
            } else {
                Log.d("HomeViewModel", "Current data: null")
            }
        }
    }

    // 체크 상태 변경 (Firestore 업데이트)
    fun updateCheckedState(item: TodoItem, isCommon: Boolean) {
        viewModelScope.launch {
            try {
                val currentList = if (isCommon) _commonList.value else _personalList.value
                val updatedList = currentList.map {
                    if (it.text == item.text) {
                        it.copy(checked = !it.checked) // 체크 상태 토글
                    } else {
                        it
                    }
                }
                val docRef = if (isCommon) commonListDocRef else personalListDocRef
                docRef.update("items", updatedList).await()
                Log.d("HomeViewModel", "Checked state updated successfully.")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error updating checked state", e)
            }
        }
    }

    // '완료' 버튼 클릭 시 전체 리스트를 Firestore에 업데이트
    fun saveLists(newCommonList: List<TodoItem>, newPersonalList: List<TodoItem>) {
        viewModelScope.launch {
            try {
                // Firestore 트랜잭션을 사용하여 두 업데이트를 원자적으로 처리
                db.runTransaction { transaction ->
                    // 문서가 없으면 새로 생성하도록 합니다.
                    val commonData = mapOf("items" to newCommonList)
                    val personalData = mapOf("items" to newPersonalList)

                    transaction.set(commonListDocRef, commonData)
                    transaction.set(personalListDocRef, personalData)
                }.await()
                Log.d("HomeViewModel", "Lists saved successfully.")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error saving lists", e)
            }
        }
    }
}