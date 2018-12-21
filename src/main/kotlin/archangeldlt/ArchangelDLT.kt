package archangeldlt

import archangeldlt.ethereum.Ethereum
import archangeldlt.pane.Monitor
import archangeldlt.pane.Search
import javafx.geometry.HPos
import javafx.scene.layout.Priority
import tornadofx.*

class GUI : App(TabBox::class)

fun main(args: Array<String>) {
    launch<GUI>(*args)
}

class TabBox : View("Archangel") {
    val ethereum = Ethereum()

    override val root = vbox {
        hbox {
            region {
                hgrow = Priority.SOMETIMES
                styleClass.add("menu-bar")
            }
            menubar {
                menu("Settings") {
                    item("Ethereum","Shortcut+E").action {
                        println ("Woot")
                    }
                }
            }
        }
        tabpane {
            gridpaneConstraints {
                vhGrow = Priority.ALWAYS
            }
            tab("Search") {
                this@tab += Search(ethereum)
                isClosable = false
            }
            tab("Monitor") {
                this@tab += Monitor(ethereum)
                isClosable = false
            }
        }
    }

    override fun onUndock() {
        ethereum.shutdown()
    }
}