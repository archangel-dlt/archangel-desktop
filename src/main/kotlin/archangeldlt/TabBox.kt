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
        tab ("Search") {
            this@tab += Search()
            isClosable = false
        }
        tab ("Monitor") {
            this@tab += Monitor(ethereum)
            isClosable = false
        }
    }
}