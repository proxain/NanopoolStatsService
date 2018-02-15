package org.chirpan.nanopoolstatservice

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader


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
        val startServiceIntent = Intent(this, MyJobService::class.java)
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


