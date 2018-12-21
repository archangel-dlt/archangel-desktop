package archangeldlt

import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.Record
import archangeldlt.pane.Monitor
import archangeldlt.pane.Search
import dialog.Settings
import javafx.scene.layout.Priority
import tornadofx.*

class GUI : App(TabBox::class)

fun main(args: Array<String>) {
    launch<GUI>(*args)
}

class ArchangelController : Controller() {
    val ethereum = Ethereum()
    val events = ethereum.events

    fun shutdown() {
        ethereum.shutdown()
    }

    fun search(searchTerm: String) : List<Record> {
        return ethereum.search(searchTerm)
    }

    fun openSettings() {
        val settings = Settings(this)
        settings.openModal()
    }

    fun updateSettings(endpoint: String, address: String) {
        ethereum.restart()
    }
}

class TabBox : View("Archangel") {
    val controller = ArchangelController()

    override val root = vbox {
        hbox {
            region {
                hgrow = Priority.SOMETIMES
                styleClass.add("menu-bar")
            }
            menubar {
                menu("Settings") {
                    item("Ethereum","Shortcut+E").action {
                        controller.openSettings()
                    }
                }
            }
        }
        tabpane {
            gridpaneConstraints {
                vhGrow = Priority.ALWAYS
            }
            tab("Search") {
                this@tab += Search(controller)
                isClosable = false
            }
            tab("Monitor") {
                this@tab += Monitor(controller)
                isClosable = false
            }
        }
    }

    override fun onUndock() {
        controller.shutdown()
    }
}