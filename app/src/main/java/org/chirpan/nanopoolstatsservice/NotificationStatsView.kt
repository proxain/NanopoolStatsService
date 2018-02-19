package org.chirpan.nanopoolstatsservice

import android.app.PendingIntent
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import org.chirpan.nanopoolstatsservice.data.Account
import java.util.*

/**
 * Created by layman on 2/18/18.
 */

class NotificationStatsView(private val context: Context, smallLayoutResId: Int, bigLayoutResId: Int) {

    val smallRemoteViews = RemoteViews(context.packageName, smallLayoutResId)
    val bigRemoteViews = RemoteViews(context.packageName, bigLayoutResId)

    lateinit var stopIntent: PendingIntent
    lateinit var refreshIntent: PendingIntent

    var isBigRemoteViewsInitialized = false

    fun setAccount(account: Account) {
        changeInitContent()

        if (!isBigRemoteViewsInitialized) {
            isBigRemoteViewsInitialized = true
        }

        setTitle(account.getTitle())
        setHashes(account.hashrate)
        setLastSync(lastSyncTime())

        setSixHourAvg(account.avgHashrate[2])
        setBalance(account.balance)

        setOnClickIntents()

        setSynced()
    }

    private fun setOnClickIntents() {
        bigRemoteViews.setOnClickPendingIntent(R.id.stop_action, stopIntent)
        bigRemoteViews.setOnClickPendingIntent(R.id.refresh_action, refreshIntent)
    }

    private fun setBalance(balance: String) {
        bigRemoteViews.setTextViewText(R.id.balance, "${balance}Ξ")
    }

    private fun setSixHourAvg(avgHashrate: String) {
        bigRemoteViews.setTextViewText(R.id.six_hour_avg, "6H:$avgHashrate Mh/s")
    }

    fun setSynced() {
        smallRemoteViews.setTextColor(R.id.sync_status, context.resources.getColor(android.R.color.holo_green_dark))
        smallRemoteViews.setTextViewText(R.id.sync_status, context.getString(R.string.synced))

        if (isBigRemoteViewsInitialized) {
            bigRemoteViews.setTextColor(R.id.sync_status, context.resources.getColor(android.R.color.holo_green_dark))
            bigRemoteViews.setTextViewText(R.id.sync_status, context.getString(R.string.synced))
        }
    }

    fun setSyncing() {
        smallRemoteViews.setTextColor(R.id.sync_status, context.resources.getColor(android.R.color.holo_red_dark))
        smallRemoteViews.setTextViewText(R.id.sync_status, context.getString(R.string.syncing))

        if (isBigRemoteViewsInitialized) {
            bigRemoteViews.setTextColor(R.id.sync_status, context.resources.getColor(android.R.color.holo_red_dark))
            bigRemoteViews.setTextViewText(R.id.sync_status, context.getString(R.string.syncing))
        }
    }

    private fun changeInitContent() {
        smallRemoteViews.setViewVisibility(R.id.sync_status, View.VISIBLE)
        smallRemoteViews.setViewVisibility(R.id.stats_info_container, View.VISIBLE)

        smallRemoteViews.setViewVisibility(R.id.sending_request, View.GONE)
    }

    fun setTitle(title: String) {
        smallRemoteViews.setTextViewText(R.id.title, title)
        if (isBigRemoteViewsInitialized) {
            bigRemoteViews.setTextViewText(R.id.title, title)
        }
    }

    fun setLastSync(lastSync: String) {
        smallRemoteViews.setTextViewText(R.id.last_sync, lastSync)
        if (isBigRemoteViewsInitialized) {
            bigRemoteViews.setTextViewText(R.id.last_sync, lastSync)
        }
    }

    fun setHashes(hashes: String) {
        smallRemoteViews.setTextViewText(R.id.hashes, "$hashes Mh/s")
        if (isBigRemoteViewsInitialized) {
            bigRemoteViews.setTextViewText(R.id.hashes, "Last:$hashes Mh/s")
        }
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
}
