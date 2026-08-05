package com.example.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AppDatabase
import com.example.data.SyncLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriveSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = AppDatabase.getDatabase(applicationContext)
        val folderPairDao = database.folderPairDao()
        val syncLogDao = database.syncLogDao()

        val pairId = inputData.getInt("FOLDER_PAIR_ID", -1)
        if (pairId == -1) return@withContext Result.failure()

        val pair = folderPairDao.getFolderPairById(pairId)
            ?: return@withContext Result.failure()

        try {
            // Log sync start
            syncLogDao.insertLog(SyncLog(folderPairId = pair.id, status = "Running", message = "Started sync for ${pair.name}"))

            // TODO: Implement actual Google Drive API sync logic here
            // 1. Get Google Account credentials
            // 2. Initialize Drive Client
            // 3. Compare local files with Drive files
            // 4. Perform upload/download based on pair.syncType
            // 5. Handle conflicts

            // Simulate work
            kotlinx.coroutines.delay(2000)

            // Update pair status
            val updatedPair = pair.copy(lastSyncStatus = "Success", lastSyncTime = System.currentTimeMillis())
            folderPairDao.updateFolderPair(updatedPair)

            syncLogDao.insertLog(SyncLog(folderPairId = pair.id, status = "Success", message = "Sync completed successfully"))

            Result.success()
        } catch (e: Exception) {
            val errorPair = pair.copy(lastSyncStatus = "Failed: ${e.message}", lastSyncTime = System.currentTimeMillis())
            folderPairDao.updateFolderPair(errorPair)
            
            syncLogDao.insertLog(SyncLog(folderPairId = pair.id, status = "Failed", message = e.message ?: "Unknown error"))
            
            Result.failure()
        }
    }
}
