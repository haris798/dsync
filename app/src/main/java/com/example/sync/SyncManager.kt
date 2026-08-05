package com.example.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncManager {
    private const val PERIODIC_SYNC_WORK_NAME = "PeriodicDriveSync"

    fun triggerSync(context: Context, pairId: Int, wifiOnly: Boolean = false) {
        val constraintsBuilder = Constraints.Builder()
        
        if (wifiOnly) {
            constraintsBuilder.setRequiredNetworkType(NetworkType.UNMETERED)
        } else {
            constraintsBuilder.setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val workRequest = OneTimeWorkRequestBuilder<DriveSyncWorker>()
            .setConstraints(constraintsBuilder.build())
            .setInputData(Data.Builder().putInt("FOLDER_PAIR_ID", pairId).build())
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun schedulePeriodicSync(context: Context, intervalMinutes: Long = 15, wifiOnly: Boolean = false) {
        val constraintsBuilder = Constraints.Builder()
        if (wifiOnly) {
            constraintsBuilder.setRequiredNetworkType(NetworkType.UNMETERED)
        } else {
            constraintsBuilder.setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val periodicWorkRequest = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraintsBuilder.build())
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }
}
