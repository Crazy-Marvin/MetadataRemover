package rocks.poopjournal.metadataremover.util.extensions

import java.util.*

fun Random.nextLong(n: Long): Long {
    if (n <= 0) throw IllegalArgumentException("n must be positive");
    var bits: Long
    var value: Long
    do {
        bits = (nextLong() shl 1).ushr(1)
        value = bits % n
    } while (bits - value + (n - 1) < 0L)
    return value
}