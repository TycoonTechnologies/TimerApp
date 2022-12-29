package com.zybooks.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters

const val KEY_MILLISECONDS_REMAINING = "com.zybooks.timer.MILLIS_LEFT"
const val CHANNEL_ID_TIMER = "channel_timer"

class TimerWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun doWork(): Result {

        // Get remaining milliseconds from MainActivity
        val remainingMillis = inputData.getLong(KEY_MILLISECONDS_REMAINING, 0)

        // Can't continue without remaining time
        if (remainingMillis == 0L) {
            return Result.failure()
        }

        // Create notification channel for all notifications
        createTimerNotificationChannel()

        // Start a new TimerModel
        val timerModel = TimerModel()
        timerModel.start(remainingMillis)

        while (timerModel.isRunning) {

            // New notification shows remaining time
            createTimerNotification()

            // Wait one second
            Thread.sleep(1000)

            if (timerModel.remainingMilliseconds == 0L) {
                timerModel.stop()

                // Last notification
                createTimerNotification()
            }
        }

        return Result.success()
    }

    private fun createTimerNotification() {

    }

    private fun createTimerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val description = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID_TIMER, name, importance)
            channel.description = description

            // Register channel with system
            notificationManager.createNotificationChannel(channel)
        }

    } }