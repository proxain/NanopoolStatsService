package org.chirpan.nanopoolstatsservice.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import org.chirpan.nanopoolstatsservice.FOREGROUND_SERVICE_NOTIFCATION_ID
import org.chirpan.nanopoolstatsservice.MESSENGER_INTENT_KEY
import org.chirpan.nanopoolstatsservice.REFRESH_JOB_ACTION
import org.chirpan.nanopoolstatsservice.STOPFOREGROUND_ACTION
import org.chirpan.nanopoolstatsservice.data.Account
import java.util.*

/**
 * Created by layman on 2/17/18.
 */

class NanopoolSyncService : Service(), SyncManger.TaskFinishedListener {

    private val syncManger = SyncManger(this)
    private lateinit var notificationManager: NotificationManager

    private var activityMessenger: Messenger? = null
    private var runningAccount: Account? = null

    private lateinit var notificationProvider: NotificationProvider


    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationProvider = NotificationProvider(applicationContext)
        notificationProvider.stopIntent = getStopIntent()
        notificationProvider.refreshIntent = getRefreshIntent()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        activityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY)

        Log.e(TAG, "onStartCommand intent: $intent")

        when (intent.action) {
            STOPFOREGROUND_ACTION -> {
                stopForeground(true)
                stopSelf()
            }
            REFRESH_JOB_ACTION -> {
                syncManger.startRefreshTask()
                postUserRefreshNotification(runningAccount != null)
            }
            else -> {
                syncManger.startRegularTask()
                startForegroundWithNotification()
            }
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    override fun onTaskFinished(account: Account?, needReschedule: Boolean) {
        if (needReschedule) {
            postUserRefreshNotification(runningAccount != null)
            return
        }

        runningAccount = account!!
        postUserInfoNotification(runningAccount!!)
    }


    private fun startForegroundWithNotification() {
        val notification = notificationProvider.getInitNotification()

        startForeground(FOREGROUND_SERVICE_NOTIFCATION_ID, notification)
    }

    private fun getRefreshIntent(): PendingIntent {
        val refreshJobIntent = Intent(this, NanopoolSyncService::class.java)
        refreshJobIntent.action = REFRESH_JOB_ACTION

        return PendingIntent.getService(applicationContext, 0, refreshJobIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getStopIntent(): PendingIntent {
        val stopForeground = Intent(this, NanopoolSyncService::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        stopForeground.action = STOPFOREGROUND_ACTION
        return PendingIntent.getService(applicationContext, 0, stopForeground, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun postUserRefreshNotification(hasAccount: Boolean) {
        val notification = notificationProvider.getUserRefreshNotification(hasAccount)
        notificationManager.notify(FOREGROUND_SERVICE_NOTIFCATION_ID, notification)

    }

    private fun postUserInfoNotification(account: Account) {
        val title = account.workers[0].id
        val hashes = account.hashrate + "Mh/s"
        val lastSync = lastSyncTime()

        val notification = notificationProvider.getUserInfoNotification(title, hashes, lastSync)
        notificationManager.notify(FOREGROUND_SERVICE_NOTIFCATION_ID, notification)
    }

    private fun lastSyncTime(): String {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var result = "@ $hour:$minute"
        if (hour > 10 && minute < 10) {
            result = "@ $hour:0$minute"
        } else if (hour < 10 && minute < 10) {
            result = "@ 0$hour:0$minute"
        } else if (hour <10 && minute > 10) {
            result = "@ 0$hour:$minute"
        }

        return result
    }

    companion object {
        private val TAG = "MyJobService"
    }
}
