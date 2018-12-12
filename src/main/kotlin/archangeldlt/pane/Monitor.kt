package archangeldlt.pane

import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.Record
import tornadofx.*

class Monitor(ethereum: Ethereum) : View("Archangel Monitor") {
    override val root = tableview(ethereum.events) {
        column("Type", Record::Tag)
        column("Key", Record::Key)
        columnResizePolicy = SmartResize.POLICY
    }
}