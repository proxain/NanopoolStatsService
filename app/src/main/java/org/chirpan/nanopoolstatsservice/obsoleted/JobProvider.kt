package org.chirpan.nanopoolstatsservice.obsoleted

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import org.chirpan.nanopoolstatsservice.REFRESH_SCHEDULED_JOB_ID
import org.chirpan.nanopoolstatsservice.REGULAR_SCHEDULED_JOB_ID
import org.chirpan.nanopoolstatsservice.SCHEDULED_JOB_REPEATE_TIME

/**
 * Created by layman on 2/16/18.
 */

class JobProvider(context: Context,
                  private val serviceComponent: ComponentName) {
    private val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

    fun scheduleRegularJob() {
        val builder = JobInfo.Builder(REGULAR_SCHEDULED_JOB_ID, serviceComponent)
        builder.setPeriodic(SCHEDULED_JOB_REPEATE_TIME)
        builder.setPersisted(true)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

        jobScheduler.schedule(builder.build())
    }

    fun scheduleRefreshJob() {
        val builder = JobInfo.Builder(REFRESH_SCHEDULED_JOB_ID, serviceComponent)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

        jobScheduler.schedule(builder.build())
    }
}
