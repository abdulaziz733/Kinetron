package com.abdulaziz733.kinetron.data.sync

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {
    private const val TAG = "SyncScheduler"
    private const val SYNC_WORK_NAME = "KinetronSyncWork"

    fun schedulePeriodicSync(context: Context) {
        Log.d(TAG, "📅 Scheduling periodic sync (every 15 mins)")
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES  // Minimum allowed interval
        )
        .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // Always update to latest config
            workRequest
        )
        Log.d(TAG, "✅ Periodic sync enqueued successfully")
    }

    /**
     * Triggers an IMMEDIATE sync via WorkManager using expedited work.
     * On Android 12+, this uses a short-lived foreground service.
     */
    fun scheduleImmediateSync(context: Context) {
        Log.d(TAG, "🚀 Scheduling IMMEDIATE expedited sync")
        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d(TAG, "✅ Immediate sync enqueued successfully")
    }

    fun cancelPeriodicSync(context: Context) {
        Log.d(TAG, "🛑 Cancelling periodic sync")
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }
}
