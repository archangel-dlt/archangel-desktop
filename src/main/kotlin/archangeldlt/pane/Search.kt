package archangeldlt.pane

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert.AlertType.INFORMATION
import tornadofx.*

class Search : View("Search Archangel") {
    val input = SimpleStringProperty()

    override val root = form {
        fieldset {
            field("<search field>") {
                textfield(input)
            }
            button("Search") {
                action {
                    alert(INFORMATION, "Well done!", input.value)
                    input.value = ""
                }
            }
        }
    }
}