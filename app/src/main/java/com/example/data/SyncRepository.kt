package com.example.data

import kotlinx.coroutines.flow.Flow

class SyncRepository(
    private val folderPairDao: FolderPairDao,
    private val syncLogDao: SyncLogDao
) {
    val allFolderPairs: Flow<List<FolderPair>> = folderPairDao.getAllFolderPairs()
    val allLogs: Flow<List<SyncLog>> = syncLogDao.getAllLogs()

    suspend fun insertFolderPair(folderPair: FolderPair) {
        folderPairDao.insertFolderPair(folderPair)
    }

    suspend fun updateFolderPair(folderPair: FolderPair) {
        folderPairDao.updateFolderPair(folderPair)
    }

    suspend fun deleteFolderPair(folderPair: FolderPair) {
        folderPairDao.deleteFolderPair(folderPair)
    }

    suspend fun getFolderPairById(id: Int): FolderPair? {
        return folderPairDao.getFolderPairById(id)
    }

    suspend fun insertLog(log: SyncLog) {
        syncLogDao.insertLog(log)
    }

    suspend fun clearLogs() {
        syncLogDao.clearLogs()
    }
}
