package archangeldlt.pane

import javafx.scene.control.Alert.AlertType.INFORMATION
import tornadofx.*

class Search : View("Hello TornadoFX") {
    override val root = borderpane {
        top {
            stackpane {
                label(title)
            }
        }
        center {
            stackpane {
                button("Click me") {
                    setOnAction {
                        alert(INFORMATION, "Well done!", "You clicked me!")
                    }
                }
            }
        }
    }
}