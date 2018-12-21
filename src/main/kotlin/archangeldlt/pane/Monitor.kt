package archangeldlt.pane

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Record
import tornadofx.*

class Monitor(controller: ArchangelController) : View("Archangel Monitor") {
    override val root = tableview(controller.events) {
        column("Block", Record::Block)
        column("Type", Record::Tag)
        column("Key", Record::Key)
        columnResizePolicy = SmartResize.POLICY
    }
}