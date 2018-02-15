/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chirpan.nanopoolstatservice

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.io.BufferedInputStream
import java.util.*
import java.util.concurrent.Executors


/**
 * Service to handle callbacks from the JobScheduler. Requests scheduled with the JobScheduler
 * ultimately land on this service's "onStartJob" method. It runs jobs for a specific amount of time
 * and finishes them. It keeps the activity updated with changes via a Messenger.
 */
class MyJobService : JobService() {

    private val executor = Executors.newSingleThreadExecutor()

    private var activityMessenger: Messenger? = null

    private lateinit var jobScheduler: JobScheduler
    private lateinit var serviceComponent: ComponentName

    override fun onCreate() {
        super.onCreate()

        jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        serviceComponent = ComponentName(packageName, MyJobService::class.java.name)
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        activityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY)
        if (intent.action == STOPFOREGROUND_ACTION) {
            stopForeground(true)
            stopSelf()
        } else {
            scheduleJob()
            postNotification()
        }
        return Service.START_STICKY
    }

    private fun scheduleJob() {
        val builder = JobInfo.Builder(SCHEDULED_JOB_ID, serviceComponent)
        builder.setPeriodic(SCHEDULED_JOB_REPEATE_TIME)
        builder.setPersisted(true)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }

    private fun postNotification() {
        val notification = getNotification()

        startForeground(FOREGROUND_SERVICE_NOTIFCATION_ID, notification)

    }

    private fun getNotification(title: String = "Notification", content: String = "Service notification"): Notification {
        return NotificationCompat.Builder(this, "NanopoolSS")
                .setContentTitle(title)
                .setTicker("Hello miner")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(getLauncherIntent())
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", getStopIntent())
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(0)
                .build()
    }

    private fun getStopIntent(): PendingIntent {
        val cancelIntent = Intent(STOPFOREGROUND_ACTION)

        return PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getLauncherIntent(): PendingIntent {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = MAIN_ACTION
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }

    private fun lastSyncTime(): String {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var result = " / Last sync at: $hour:$minute"
        if (minute < 10) {
            result = " / Last sync at: $hour:0$minute"
        }

        return result
    }

    override fun onStartJob(params: JobParameters): Boolean {
        // The work that this service "does" is simply wait for a certain duration and finish
        // the job (on another thread).
        Log.i(TAG, "on start job: ${params.jobId}")

        executor.execute({ refreshInfo(params)})

        return true
    }

    private fun refreshInfo(params: JobParameters) {
        val account = getAccount()
        updateNotification(account)
//        val lastOnline = getLastOnline(accInfo.workers[0].lastShare)

        jobFinished(params, account == null)
        Log.i(TAG, "jobFinished needsReschedule: ${account == null}")
    }

    private fun updateNotification(account: Account?) {
        val title = account?.workers?.get(0)?.id + lastSyncTime()
        val content = "Curr Mh/s: ${account?.hashrate} / 6h Avg Mh/s: ${account?.avgHashrate?.get(2)}"

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotification(title, content)
        notificationManager.notify(FOREGROUND_SERVICE_NOTIFCATION_ID, notification)
    }

    private fun getAccount(): Account? {
        val networkClient = NetworkClient()
        val stream = BufferedInputStream(
                networkClient.get("https://api.nanopool.org/v1/eth/user/$ETH_ADDRESS"))
        return NanopoolParser().readJson(stream)
    }

    fun getLastOnline(lastTime: Long): String {
        val lastMs = System.currentTimeMillis() - lastTime
        val minutes = (lastMs / (1000 * 60) % 60).toInt()
        val hours = (lastMs / (1000 * 60 * 60) % 24).toInt()

        return "${hours}h ${minutes}m"
    }

    override fun onStopJob(params: JobParameters): Boolean {
        // Stop tracking these job parameters, as we've 'finished' executing.
//        sendMessage(MSG_COLOR_STOP, params.jobId)
        Log.i(TAG, "on stop job: ${params.jobId}")
//        stopForeground(true)

        // Return false to drop the job.
        return false
    }

    private fun sendMessage(messageID: Int, params: Any?) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (activityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }
        val message = Message.obtain()
        message.run {
            what = messageID
            obj = params
        }
        try {
            activityMessenger?.send(message)
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    companion object {
        private val TAG = "MyJobService"
    }
}
