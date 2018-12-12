package archangeldlt.ethereum

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class Record (val tag: String, val key: String, val timestamp: String) {
    fun Tag() : StringProperty { return SimpleStringProperty(tag) }
    fun Key() : StringProperty { return SimpleStringProperty(key) }
    fun Timestamp() : StringProperty { return SimpleStringProperty(timestamp) }
}