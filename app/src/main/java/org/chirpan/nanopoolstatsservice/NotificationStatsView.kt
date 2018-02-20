package org.chirpan.nanopoolstatsservice

import android.app.PendingIntent
import android.content.Context
import android.graphics.*
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

        smallRemoteViews.setViewVisibility(R.id.hashes, View.VISIBLE)
        smallRemoteViews.setViewVisibility(R.id.last_sync, View.VISIBLE)
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

    private fun lastSyncTime(currentTime: Long = System.currentTimeMillis()): String {
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

    fun setShareHistory(shareRateTable: List<Pair<Long, Int>>) {
        val latest = shareRateTable.subList(0, 5)
        val max = latest.map { it.second }.max() ?: 0

        drawLatest(latest, max)
    }

    private fun drawLatest(latest: List<Pair<Long, Int>>, max: Int) {
        val sharesIdsArr = arrayListOf(R.id.line_1_shares, R.id.line_2_shares, R.id.line_3_shares, R.id.line_4_shares, R.id.line_5_shares)
        val timesIdsArr = arrayListOf(R.id.line_1_time, R.id.line_2_time, R.id.line_3_time, R.id.line_4_time, R.id.line_5_time)
        val chartIdsArr = arrayListOf(R.id.line_1_bar, R.id.line_2_bar, R.id.line_3_bar, R.id.line_4_bar, R.id.line_5_bar)

        for (i in latest.indices) {
            val bar = getBarImage(latest[i], max)
            bigRemoteViews.setImageViewBitmap(chartIdsArr[i], bar)
            bigRemoteViews.setTextViewText(timesIdsArr[i], lastSyncTime(latest[i].first))
            bigRemoteViews.setTextViewText(sharesIdsArr[i], "${latest[i].second}")
        }
    }

    private fun getBarImage(late: Pair<Long, Int>, max: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 1, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val right = late.second * (100 / max)
        val rect = Rect(0, 0, right, 1)
        val paint = Paint()
        paint.color = Color.GREEN

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.drawRect(rect, paint)

        return bitmap
    }
}
