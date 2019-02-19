package archangeldlt.pane

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Record
import tornadofx.*

class Monitor(controller: ArchangelController) : View("Archangel Monitor") {
    override val root = tableview(controller.events) {
        readonlyColumn("Block", Record::block)
        readonlyColumn("Type", Record::tag)
        readonlyColumn("Record", Record::asString)
        columnResizePolicy = SmartResize.POLICY
    }
}