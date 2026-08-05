package com.example.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PeriodicSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = AppDatabase.getDatabase(applicationContext)
        val folderPairDao = database.folderPairDao()
        
        try {
            // Get all folder pairs that have autoSync enabled
            val autoSyncPairs = folderPairDao.getAutoSyncFolderPairs()
            
            for (pair in autoSyncPairs) {
                SyncManager.triggerSync(applicationContext, pair.id)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
