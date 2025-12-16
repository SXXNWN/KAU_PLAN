package com.example.kau_plan

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Date

data class ReservationData(
    val itemTitle: String = "",
    val studentName: String = "",
    val studentId: String = "",
    val reservationTime: String? = null,
    val fridgeContent: String? = null,
    val createdAt: Date = Date()
)

class ReservationViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun saveReservation(
        reservation: ReservationData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("reservations")
            .add(reservation)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}