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
              fileList: JsonArray
) {
    private val info = Package(key, data, fileList)

    val citation = info.citation
    val supplier = info.supplier
    val creator = info.creator
    val rights = info.rights
    val held = info.held
    val files = info.files
}