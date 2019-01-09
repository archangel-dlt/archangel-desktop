package archangeldlt.ethereum

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.ItemViewModel
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
) : ItemViewModel<Package>(Package()) {
    init {
        item.fromEvent(key, data, fileList)
    }


    fun Block() : IntegerProperty { return SimpleIntegerProperty(block.intValueExact()) }
    fun Tag() : StringProperty { return SimpleStringProperty(tag) }
    fun Key() : StringProperty { return SimpleStringProperty(key) }
    fun Timestamp() : StringProperty { return SimpleStringProperty(timestamp) }

    val citation = bind { item.citationProperty }
    val supplier = bind { item.supplierProperty }
    val creator = bind { item.creatorProperty }
    val rights = bind { item.rightsProperty }
    val held = bind { item.heldProperty }
    val files = item.files
}