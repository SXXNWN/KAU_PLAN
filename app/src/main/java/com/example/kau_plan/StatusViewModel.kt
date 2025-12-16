package com.example.kau_plan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SimpleReservation(
    val id: String,
    val itemTitle: String,
    val studentName: String
)

data class MachineStatus(
    val isWasherA_InUse: Boolean = false,
    val isWasherB_InUse: Boolean = false,
    val isWasherC_InUse: Boolean = false,
    val isDryerA_InUse: Boolean = false,
    val isDryerB_InUse: Boolean = false,
    val isDryerC_InUse: Boolean = false,
)

class StatusViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _reservations = mutableStateOf<List<SimpleReservation>>(emptyList())
    val reservations: State<List<SimpleReservation>> = _reservations

    private val _machineStatus = mutableStateOf(MachineStatus())
    val machineStatus: State<MachineStatus> = _machineStatus

    private val _fridgeItems = mutableStateOf<List<FridgeItem>>(emptyList())
    val fridgeItems: State<List<FridgeItem>> = _fridgeItems

    init {
        listenToRecentReservations()
        listenToCurrentMachineStatus()
        listenToFridgeItems()
    }

    private fun listenToRecentReservations() {
        db.collection("reservations")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    val reservationList = snapshots.documents.mapNotNull { doc ->
                        doc.toObject(ReservationData::class.java)?.let {
                            SimpleReservation(doc.id, it.itemTitle, it.studentName)
                        }
                    }
                        .filter { !it.itemTitle.contains("신고") }
                        .take(3)

                    _reservations.value = reservationList
                }
            }
    }

    private fun listenToCurrentMachineStatus() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val nextHour = hour + 1
        val currentTimeSlot = String.format(Locale.KOREA, "%02d:00 ~ %02d:00", hour, nextHour)

        db.collection("reservations")
            .whereEqualTo("reservationTime", currentTimeSlot)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    val machinesInUse = snapshots.documents.mapNotNull { doc ->
                        doc.toObject(ReservationData::class.java)?.itemTitle
                    }.toSet()

                    _machineStatus.value = MachineStatus(
                        isWasherA_InUse = machinesInUse.contains("세탁기 - A"),
                        isWasherB_InUse = machinesInUse.contains("세탁기 - B"),
                        isWasherC_InUse = machinesInUse.contains("세탁기 - C"),
                        isDryerA_InUse = machinesInUse.contains("건조기 - A"),
                        isDryerB_InUse = machinesInUse.contains("건조기 - B"),
                        isDryerC_InUse = machinesInUse.contains("건조기 - C")
                    )
                }
            }
    }

    private fun listenToFridgeItems() {
        val dateFormat = SimpleDateFormat("MM/dd", Locale.KOREA)

        db.collection("reservations")
            .whereIn("itemTitle", listOf("출입 신고", "반출 신고"))
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    val items = snapshots.documents.mapNotNull { doc ->
                        val data = doc.toObject(ReservationData::class.java)
                        if (data != null) {
                            FridgeItem(
                                id = doc.id,
                                owner = data.studentName,
                                itemName = data.fridgeContent ?: data.itemTitle,
                                date = dateFormat.format(data.createdAt)
                            )
                        } else null
                    }
                    _fridgeItems.value = items
                }
            }
    }

    fun useMachineNow(machineName: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val nextHour = hour + 1
        val currentTimeSlot = String.format(Locale.KOREA, "%02d:00 ~ %02d:00", hour, nextHour)

        val reservation = ReservationData(
            itemTitle = machineName,
            studentName = "현장 사용",
            studentId = "00000000",
            reservationTime = currentTimeSlot,
            createdAt = Date()
        )

        db.collection("reservations").add(reservation)
    }
}