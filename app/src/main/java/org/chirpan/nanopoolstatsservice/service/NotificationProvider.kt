package org.chirpan.nanopoolstatsservice.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.*
import android.support.v4.app.NotificationCompat
import android.view.View
import android.widget.RemoteViews
import org.chirpan.nanopoolstatsservice.R

/**
 * Created by layman on 2/16/18.
 */
class NotificationProvider(private val context: Context){

    lateinit var stopIntent: PendingIntent
    lateinit var refreshIntent: PendingIntent

    private var contentView = RemoteViews(context.packageName, R.layout.notification_layout)
    private var expandedView= RemoteViews(context.packageName, R.layout.big_notification_layout)

    fun getInitNotification(): Notification {
        val builder = getGenericBuilder(stopIntent)

        builder.setContent(contentView)
        builder.setCustomBigContentView(contentView)

        return builder.build()
    }

    fun getErrorRefreshNotification(): Notification {
        val builder = getGenericBuilder(stopIntent)

        contentView.setTextViewText(R.id.title, context.getString(R.string.init_notify_fail_title))
        contentView.setTextViewText(R.id.text, context.getString(R.string.init_notify_fail_text))

        builder.setContent(contentView)
        builder.setCustomBigContentView(contentView)

        return builder.build()
    }

    fun getUserInfoNotification(title: String,
                                hashes: String,
                                lastSync: String): Notification {
        val builder = getGenericBuilder(stopIntent, refreshIntent)

        contentView.setTextViewText(R.id.title, title)
        contentView.setTextViewText(R.id.hashes, hashes)
        contentView.setTextViewText(R.id.last_sync, lastSync)

        contentView.setViewVisibility(R.id.text, View.GONE)
        contentView.setViewVisibility(R.id.stats_info_container, View.VISIBLE)
        contentView.setViewVisibility(R.id.sync_status, View.VISIBLE)

        contentView.setTextColor(R.id.sync_status, context.resources.getColor(android.R.color.holo_green_dark))
        contentView.setTextViewText(R.id.sync_status, context.getString(R.string.synced))

        expandedView.setTextViewText(R.id.title, title)
        expandedView.setTextViewText(R.id.hashes, hashes)
        expandedView.setTextViewText(R.id.last_sync, lastSync)

        expandedView.setViewVisibility(R.id.text, View.GONE)
        expandedView.setViewVisibility(R.id.stats_info_container, View.VISIBLE)
        expandedView.setViewVisibility(R.id.sync_status, View.VISIBLE)

        expandedView.setOnClickPendingIntent(R.id.stop_action, stopIntent)
        expandedView.setOnClickPendingIntent(R.id.refresh_action, refreshIntent)

        builder.setContent(contentView)
        builder.setCustomBigContentView(expandedView)

        return builder.build()
    }

    fun getUserRefreshNotification(hasAccount: Boolean): Notification {
        val builder = getGenericBuilder(stopIntent)

        contentView.setTextColor(R.id.sync_status, context.resources.getColor(android.R.color.holo_red_dark))
        contentView.setTextViewText(R.id.sync_status, context.getString(R.string.syncing))
        if (hasAccount) {
            contentView.setTextViewText(R.id.title, context.getString(R.string.init_notify_fail_title))
        }

        builder.setContent(contentView)
        builder.setCustomBigContentView(expandedView)

        return builder.build()
    }

    private fun getGenericBuilder(stopIntent: PendingIntent, refreshIntent: PendingIntent? = null): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, "NanopoolSS")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(R.drawable.ic_highlight_off_black_24dp, context.getString(R.string.notify_btn_stop), stopIntent)
                .setWhen(0)

        refreshIntent?.let {
            builder.addAction(R.drawable.ic_restore_black_24dp, context.getString(R.string.notify_btn_refresh), refreshIntent)
        }
        return builder
    }

    private fun generateActionButton(iconResId: Int, title: CharSequence, actionIntent: PendingIntent? = null): RemoteViews {
        val tombstone = actionIntent == null
        val button = RemoteViews(context.packageName,
                if (tombstone)
                    android.support.compat.R.layout.notification_action_tombstone
                else
                    android.support.compat.R.layout.notification_action)
        button.setImageViewBitmap(android.support.compat.R.id.action_image,
                createColoredBitmap(iconResId, context.resources
                        .getColor(android.support.compat.R.color.notification_action_color_filter)))
        button.setTextViewText(android.support.compat.R.id.action_text, title)
        if (!tombstone) {
            button.setOnClickPendingIntent(android.support.compat.R.id.action_container, actionIntent)
        }

        button.setContentDescription(android.support.compat.R.id.action_container, title)

        return button
    }

    private fun createColoredBitmap(iconId: Int, color: Int, size: Int = 0): Bitmap {
        val drawable = context.resources.getDrawable(iconId)
        val width = if (size == 0) drawable.intrinsicWidth else size
        val height = if (size == 0) drawable.intrinsicHeight else size
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, width, height)
        if (color != 0) {
            drawable.mutate().colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        val canvas = Canvas(resultBitmap)
        drawable.draw(canvas)
        return resultBitmap
    }
}