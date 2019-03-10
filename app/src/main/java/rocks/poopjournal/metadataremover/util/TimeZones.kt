package rocks.poopjournal.metadataremover.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZoneId
import java.util.*

object TimeZones {

    operator fun get(id: String): TimeZone = TimeZone.getTimeZone(id)

    @RequiresApi(Build.VERSION_CODES.O)
    operator fun get(id: ZoneId): TimeZone = TimeZone.getTimeZone(id)

    val UTC = get("UTC")
    val GMT = get("GMT")
    val ACT = get("ACT")
    val AET = get("AET")
    val AGT = get("AGT")
    val ART = get("ART")
    val AST = get("AST")
    val BET = get("BET")
    val BST = get("BST")
    val CAT = get("CAT")
    val CNT = get("CNT")
    val CST = get("CST")
    val CTT = get("CTT")
    val EAT = get("EAT")
    val ECT = get("ECT")
    val IET = get("IET")
    val IST = get("IST")
    val JST = get("JST")
    val MIT = get("MIT")
    val NET = get("NET")
    val NST = get("NST")
    val PLT = get("PLT")
    val PNT = get("PNT")
    val PRT = get("PRT")
    val PST = get("PST")
    val SST = get("SST")
    val VST = get("VST")
    val EST = get("EST")
    val MST = get("MST")
    val HST = get("HST")
}