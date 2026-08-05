package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_logs")
data class SyncLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val folderPairId: Int?,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String,
    val message: String
)
