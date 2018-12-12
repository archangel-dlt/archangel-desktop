package archangeldlt.ethereum

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import java.math.BigInteger

class Record (val block: BigInteger,
              val sender: String,
              val tag: String,
              val key: String,
              val timestamp: String,
              val data: JsonObject,
              val files: JsonArray<JsonObject>) {
    fun Block() : IntegerProperty { return SimpleIntegerProperty(block.intValueExact()) }
    fun Tag() : StringProperty { return SimpleStringProperty(tag) }
    fun Key() : StringProperty { return SimpleStringProperty(key) }
    fun Timestamp() : StringProperty { return SimpleStringProperty(timestamp) }
}