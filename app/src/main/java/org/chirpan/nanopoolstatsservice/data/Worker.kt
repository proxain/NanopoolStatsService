package org.chirpan.nanopoolstatsservice.data

import java.util.*

/**
 * Created by layman on 2/14/18.
 */
data class Worker(val id: String,
                  val uid: String,
                  val lastShare: Long,
                  val rating: Int,
                  val avgHashrate: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Worker

        if (id != other.id) return false
        if (uid != other.uid) return false
        if (lastShare != other.lastShare) return false
        if (rating != other.rating) return false
        if (!Arrays.equals(avgHashrate, other.avgHashrate)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uid.hashCode()
        result = 31 * result + lastShare.hashCode()
        result = 31 * result + rating
        result = 31 * result + Arrays.hashCode(avgHashrate)
        return result
    }
}