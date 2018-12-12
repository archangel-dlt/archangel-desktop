package archangeldlt.pane

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.layout.Priority
import tornadofx.*

class Search : View("Search Archangel") {
    val input = SimpleStringProperty()

    override val root = vbox {
        hbox {
            textfield(input) {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
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