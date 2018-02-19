package org.chirpan.nanopoolstatsservice.service

import android.util.Log
import org.chirpan.nanopoolstatsservice.BuildConfig
import org.chirpan.nanopoolstatsservice.ETH_ADDRESS
import org.chirpan.nanopoolstatsservice.TASK_FAIL_REFRESH_TIME
import org.chirpan.nanopoolstatsservice.TASK_REFRESH_TIME
import org.chirpan.nanopoolstatsservice.data.Account
import java.io.BufferedInputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by layman on 2/17/18.
 */

class SyncManger(private val taskFinishedListener: TaskFinishedListener,
                 private val autoRefresh: Boolean = true) {
    private val executor = Executors.newScheduledThreadPool(2)

    fun startRegularTask() {
        executor.execute({requestInfo(autoRefresh)})
    }

    private fun requestInfo(refresh: Boolean = false) {
        if (DEBUG) {
            Log.i(TAG, "requestInfo(refresh = $refresh)")
        }
        val account = getAccount()

        val needReschedule = account == null
        taskFinishedListener.onTaskFinished(account, needReschedule)

        if (DEBUG) {
            Log.i(TAG, "refreshInfo: generalInfo finished needReschedule: $needReschedule")
        }

        if (!refresh && !needReschedule) {
            return
        }

        val refreshTime = if (needReschedule) TASK_FAIL_REFRESH_TIME else TASK_REFRESH_TIME
        executor.schedule({requestInfo()}, refreshTime, TimeUnit.SECONDS)

        if (DEBUG) {
            Log.i(TAG, "refreshInfo: generalInfo next scheduled after: $refreshTime s.")
        }
    }

    fun startRefreshTask() {
        executor.execute({requestInfo()})
    }

    private fun getAccount(): Account? {
        val networkClient = NetworkClient()
        val stream = BufferedInputStream(
                networkClient.get("https://api.nanopool.org/v1/eth/user/${ETH_ADDRESS}"))
        return NanopoolParser().readJson(stream)
    }

    interface TaskFinishedListener {
        fun onTaskFinished(account: Account? = null, needReschedule: Boolean)
    }

    companion object {
        val TAG = "SyncService"
        val DEBUG = BuildConfig.DEBUG
    }
}
