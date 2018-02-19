package org.chirpan.nanopoolstatsservice.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.view.View
import android.widget.RemoteViews
import org.chirpan.nanopoolstatsservice.NotificationStatsView
import org.chirpan.nanopoolstatsservice.R
import org.chirpan.nanopoolstatsservice.data.Account


/**
 * Created by layman on 2/16/18.
 */
class NotificationProvider(private val context: Context){

    private var contentView = RemoteViews(context.packageName, R.layout.notification_layout)
    private var expandedView= RemoteViews(context.packageName, R.layout.big_notification_layout)

    private var notificationStatsView = NotificationStatsView(context,
            R.layout.notification_layout,
            R.layout.big_notification_layout)



    fun getInitNotification(): Notification {
        val builder = getGenericBuilder()

        val content = notificationStatsView.smallRemoteViews
        builder.setContent(content)

        return builder.build()
    }

    fun getErrorRefreshNotification(): Notification {
        val builder = getGenericBuilder()

        contentView.setTextViewText(R.id.title, context.getString(R.string.init_notify_fail_title))
        contentView.setTextViewText(R.id.text, context.getString(R.string.init_notify_fail_text))

        builder.setContent(contentView)
        builder.setCustomBigContentView(contentView)

        return builder.build()
    }

    fun getUserInfoNotification(account: Account): Notification {
        val builder = getGenericBuilder()

//        contentView.setTextViewText(R.id.title, title)
//        contentView.setTextViewText(R.id.hashes, hashes)
//        contentView.setTextViewText(R.id.last_sync, lastSync)
//
//        contentView.setViewVisibility(R.id.text, View.GONE)
//        contentView.setViewVisibility(R.id.stats_info_container, View.VISIBLE)
//        contentView.setViewVisibility(R.id.sync_status, View.VISIBLE)
//
//        contentView.setTextColor(R.id.sync_status, context.resources.getColor(android.R.color.holo_green_dark))
//        contentView.setTextViewText(R.id.sync_status, context.getString(R.string.synced))
//
//        expandedView.setTextViewText(R.id.title, title)
//        expandedView.setTextViewText(R.id.six_hour_avg, sixHourAvg)
//        expandedView.setTextViewText(R.id.hashes, "Last: $hashes")
//        expandedView.setTextViewText(R.id.balance, "${balance}Ξ")
//        expandedView.setTextViewText(R.id.last_sync, lastSync)
//
//        expandedView.setViewVisibility(R.id.text, View.GONE)
//        expandedView.setViewVisibility(R.id.stats_info_container, View.VISIBLE)
//        expandedView.setViewVisibility(R.id.sync_status, View.VISIBLE)
//
//        expandedView.setOnClickPendingIntent(R.id.stop_action, stopIntent)
//        expandedView.setOnClickPendingIntent(R.id.refresh_action, refreshIntent)

        notificationStatsView.setAccount(account)

        builder.setContent(notificationStatsView.smallRemoteViews)
        builder.setCustomBigContentView(notificationStatsView.bigRemoteViews)

        return builder.build()
    }

    fun getUserRefreshNotification(hasAccount: Boolean): Notification {
        val builder = getGenericBuilder()

        notificationStatsView.setSyncing()

        if (hasAccount) {
            notificationStatsView.setTitle(context.getString(R.string.init_notify_fail_title))
        }

        builder.setContent(notificationStatsView.bigRemoteViews)
        if (notificationStatsView.isBigRemoteViewsInitialized) {
            builder.setCustomBigContentView(expandedView)
        }

        return builder.build()
    }

    private fun getGenericBuilder(): NotificationCompat.Builder {

        return NotificationCompat.Builder(context, "NanopoolSS")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(0)
    }

    fun setIntents(stopIntent: PendingIntent, refreshIntent: PendingIntent) {
        notificationStatsView.stopIntent = stopIntent
        notificationStatsView.refreshIntent = refreshIntent
    }
}