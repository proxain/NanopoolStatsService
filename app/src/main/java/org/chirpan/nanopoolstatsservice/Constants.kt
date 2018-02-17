/**
 * Created by layman on 2/14/18.
 */
@file:Suppress("PropertyName")
@file:JvmName("Constants")

package org.chirpan.nanopoolstatsservice

const val MESSENGER_INTENT_KEY = "${BuildConfig.APPLICATION_ID}.MESSENGER_INTENT_KEY"
const val STOPFOREGROUND_ACTION = "${BuildConfig.APPLICATION_ID}.STOP_FOREGROUND"
const val REFRESH_JOB_ACTION = "${BuildConfig.APPLICATION_ID}.REFRESH_JOB_ACTION"
const val FOREGROUND_SERVICE_NOTIFCATION_ID = 101
const val REGULAR_SCHEDULED_JOB_ID = 551948
const val REFRESH_SCHEDULED_JOB_ID = 561948
const val SCHEDULED_JOB_REPEATE_TIME = 900000L
const val TASK_REFRESH_TIME = 600L
const val TASK_FAIL_REFRESH_TIME = 10L