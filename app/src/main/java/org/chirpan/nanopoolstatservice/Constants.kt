/**
 * Created by layman on 2/14/18.
 */
@file:Suppress("PropertyName")
@file:JvmName("Constants")

package org.chirpan.nanopoolstatservice

@JvmField val ETH_ADDRESS = ":address"

@JvmField val MESSENGER_INTENT_KEY = "${BuildConfig.APPLICATION_ID}.MESSENGER_INTENT_KEY"
@JvmField val MAIN_ACTION = "com.truiton.foregroundservice.action.main"
@JvmField val STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground"
@JvmField val STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground"
@JvmField val FOREGROUND_SERVICE_NOTIFCATION_ID = 101
@JvmField val SCHEDULED_JOB_ID = 551948
@JvmField val SCHEDULED_JOB_REPEATE_TIME = 900000L