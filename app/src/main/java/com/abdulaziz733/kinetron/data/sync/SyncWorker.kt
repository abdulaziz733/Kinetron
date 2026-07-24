package com.abdulaziz733.kinetron.data.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.abdulaziz733.kinetron.R
import com.abdulaziz733.kinetron.data.repository.DeviceDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: DeviceDataRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
        private const val CHANNEL_ID = "kinetron_sync_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "========================================")
        Log.d(TAG, "🔄 SyncWorker.doWork() STARTED")
        Log.d(TAG, "   Run attempt: ${runAttemptCount}")
        Log.d(TAG, "   Worker ID: ${id}")
        Log.d(TAG, "========================================")

        return try {
            val results = repository.syncAll(isBackground = true)

            results.forEach { (key, result) ->
                if (result.isSuccess) {
                    Log.d(TAG, "   ✅ $key: synced ${result.getOrNull() ?: 0} items")
                } else {
                    Log.e(TAG, "   ❌ $key: ${result.exceptionOrNull()?.message}")
                }
            }

            // Auto capture location
            repository.captureLocation()

            Log.d(TAG, "========================================")
            Log.d(TAG, "✅ SyncWorker.doWork() COMPLETED SUCCESSFULLY")
            Log.d(TAG, "========================================")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "❌ SyncWorker.doWork() FAILED: ${e.message}", e)
            Result.retry()
        }
    }

    /**
     * Required for setExpedited() on Android 12+.
     * Creates a foreground notification so the OS allows immediate execution.
     */
    override suspend fun getForegroundInfo(): ForegroundInfo {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Kinetron Sync")
            .setContentText("Syncing device data...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Kinetron Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background sync notifications"
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
