package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder_pairs")
data class FolderPair(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val localUri: String,
    val driveFolderId: String,
    val driveFolderName: String,
    val syncType: SyncType,
    val autoSync: Boolean = true,
    val lastSyncStatus: String = "Not synced",
    val lastSyncTime: Long = 0L
)

enum class SyncType {
    TWO_WAY,
    UPLOAD_ONLY,
    DOWNLOAD_ONLY
}
