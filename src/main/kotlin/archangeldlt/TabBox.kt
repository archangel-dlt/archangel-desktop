package archangeldlt

import archangeldlt.pane.Search
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

class TabBox : View("Archangel") {
    override val root = tabpane {
        gridpaneConstraints {
            vhGrow = Priority.ALWAYS
        }
        tab ("Search") {
            vbox {
                this@tab += Search()
            }
        }
    }
}