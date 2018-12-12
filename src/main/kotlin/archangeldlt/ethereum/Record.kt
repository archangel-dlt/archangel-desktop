package archangeldlt.ethereum

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class Record (val tag: String) {
    fun Tag() : StringProperty {
        val p = SimpleStringProperty(this, "tag")
        p.set(tag)
        return p
    }
}