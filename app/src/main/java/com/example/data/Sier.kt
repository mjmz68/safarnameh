package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sier_table")
data class Sier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateYear: Int,
    val dateMonth: Int,
    val dateDay: Int,
    val trainName: String,
    val routeFrom: String,
    val routeTo: String,
    val wagonNumber: String,
    val salonOutbound: String,
    val salonReturn: String,
    val trainMaster: String,
    val headAttendant: String,
    val memories: String = "",
    val photoUrisJson: String = "[]", // JSON of string URIs or paths
    val wageEstimate: Long = 0,      // Optional estimated wage or hours reward
    val rating: Float = 5.0f,         // Satisfaction rating of travel conditions
    val isCompleted: Boolean = true   // Completed vs Scheduled/Upcoming
) : Serializable {
    fun getFormattedDate(): String = "$dateYear/${dateMonth.toString().padStart(2, '0')}/${dateDay.toString().padStart(2, '0')}"
}
