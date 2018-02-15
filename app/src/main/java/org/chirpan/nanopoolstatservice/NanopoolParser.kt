package org.chirpan.nanopoolstatservice

import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader

/**
 * Created by layman on 2/13/18.
 */
class NanopoolParser {

    fun readJson(inputStream: BufferedInputStream): Account {
        val jsonData = readStream(inputStream)
        val response = JSONObject(jsonData)

        return parse(response)
    }

    private fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream) as Reader?)
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    private fun parse(response: JSONObject): Account {
        val status = getStatus(response)
        if (!status) {
            Log.e("NanopoolParser", "status not true")
        }

        return parseData(response)
    }

    private fun parseData(response: JSONObject): Account {
        val data = response.getJSONObject("data")
        val account = data.getString("account")
        val unconfirmed_balance = data.getString("unconfirmed_balance")
        val balance = data.getString("balance")
        val hashrate = data.getString("hashrate")

        val nanopool_account = Account(account, unconfirmed_balance, balance, hashrate)
        nanopool_account.avgHashrate = getAvgHashrate(data, "avgHashrate")
        nanopool_account.workers = getWorkers(data)

        return nanopool_account
    }

    private fun getWorkers(data: JSONObject): ArrayList<Worker> {
        val workers = data.getJSONArray("workers")
        val workersArr = ArrayList<Worker>()
        (0..(workers.length() - 1)).mapTo(destination = workersArr) { getWorker(workers.getJSONObject(it)) }
        return workersArr
    }

    private fun getWorker(workerObj: JSONObject): Worker {
        val id = workerObj.getString("id")
        val uid = workerObj.getString("uid")
        val lastShare = workerObj.getLong("lastshare")
        val rating = workerObj.getInt("rating")

        return Worker(id, uid, lastShare, rating, getAvgHashrate(workerObj))
    }

    private fun getAvgHashrate(data: JSONObject, fromObjName: String? = null): Array<String> {
        var avgHashrate = data
        if (fromObjName != null) {
            avgHashrate = data.getJSONObject(fromObjName)
        }

        val hashrateArr = arrayOf(
            avgHashrate.getString("h1"),
            avgHashrate.getString("h3"),
            avgHashrate.getString("h6"),
            avgHashrate.getString("h12"),
            avgHashrate.getString("h24")
        )

        return hashrateArr
    }

    private fun getStatus(res: JSONObject): Boolean {
        return res.getString("status").toBoolean()
    }
}