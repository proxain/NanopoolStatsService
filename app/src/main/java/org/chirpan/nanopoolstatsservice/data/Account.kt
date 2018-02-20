package org.chirpan.nanopoolstatsservice.data

/**
 * Created by layman on 2/13/18.
 */
data class Account(val account: String,
                   val unconfirmed_balance: String,
                   val balance: String,
                   val hashrate: String,
                   val status: Boolean = false) {
    lateinit var avgHashrate: Array<String>
    lateinit var workers: ArrayList<Worker>

    lateinit var shareRateTable: List<Pair<Long, Int>>

    fun getTitle(): String {
        return workers[0].id
    }

    override fun toString(): String {
        var result = super.toString() +
                "\n account: $account\n" +
                "unconfirmed_balance: $unconfirmed_balance\n" +
                "balance: $balance\n" +
                "hashrate: $hashrate" + "" +
                "avgHashrate:\n"
        for (hs in avgHashrate) {
            result += "\t$hs\n"
        }

        result += "workers:\n"

        for (worker in workers) {
            result += "\t$worker\n"
        }

        return result
    }
}