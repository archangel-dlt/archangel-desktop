package archangeldlt

import archangeldlt.ethereum.Ethereum
import archangeldlt.pane.Monitor
import archangeldlt.pane.Search
import javafx.scene.layout.Priority
import tornadofx.*

class TabBox : View("Archangel") {
    val ethereum = Ethereum()

    override val root = tabpane {
        gridpaneConstraints {
            vhGrow = Priority.ALWAYS
        }
        tab ("Monitor") {
            vbox {
                this@tab += Monitor(ethereum)
            }
        }
        tab ("Search") {
            vbox {
                this@tab += Search()
            }
        }
    }
}