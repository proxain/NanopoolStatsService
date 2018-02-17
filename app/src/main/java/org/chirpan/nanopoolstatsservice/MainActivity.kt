package org.chirpan.nanopoolstatsservice

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.chirpan.nanopoolstatsservice.service.NanopoolSyncService


class MainActivity : AppCompatActivity() {

//    private lateinit var serviceComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        serviceComponent = ComponentName(this, MyJobService::class.java)

//        val builder = JobInfo.Builder(SCHEDULED_JOB_ID, serviceComponent)
//        builder.setPeriodic(SCHEDULED_JOB_REPEATE_TIME)
//        builder.setPersisted(true)
//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//
//        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//        jobScheduler.schedule(builder.build())
    }

    override fun onStart() {
        super.onStart()
        val startServiceIntent = Intent(this, NanopoolSyncService::class.java)
//        val messengerIncoming = Messenger(handler)
//        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
        startService(startServiceIntent)
        finish()
    }

    override fun onStop() {
        super.onStop()
//        stopService(Intent(this, MyJobService::class.java))
    }
}


