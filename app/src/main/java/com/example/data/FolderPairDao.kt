package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderPairDao {
    @Query("SELECT * FROM folder_pairs")
    fun getAllFolderPairs(): Flow<List<FolderPair>>

    @Query("SELECT * FROM folder_pairs WHERE autoSync = 1")
    suspend fun getAutoSyncFolderPairs(): List<FolderPair>

    @Query("SELECT * FROM folder_pairs WHERE id = :id")
    suspend fun getFolderPairById(id: Int): FolderPair?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolderPair(folderPair: FolderPair)

    @Update
    suspend fun updateFolderPair(folderPair: FolderPair)

    @Delete
    suspend fun deleteFolderPair(folderPair: FolderPair)
}
