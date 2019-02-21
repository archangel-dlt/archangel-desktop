package archangeldlt.ethereum

import java.math.BigInteger
import javax.json.JsonArray
import javax.json.JsonObject


class Record (val block: BigInteger,
              val sender: String,
              val tag: String,
              val key: String,
              val timestamp: String,
              data: JsonObject,
              fileList: JsonArray,
              val owned: Boolean
) {
    val info = Package.fromEvent(key, data, fileList)

    val title = info.title
    val ref = info.ref
    val citation = info.citation
    val supplier = info.supplier
    val creator = info.creator
    val rights = info.rights
    val held = info.held
    val files = info.files

    val isSip = info.isSip
    val isAip = info.isAip

    val hasFilenames = info.hasFilenames()
    val hasUuid = info.hasUuid()

    val asString = toString()

    override fun toString(): String {
        val type = if (isSip) "SIP" else "AIP"
        val label = if (title.isNotEmpty()) title else if (citation.isNotEmpty()) citation else supplier
        return "${type} - ${key}: ${label}"
    }
}