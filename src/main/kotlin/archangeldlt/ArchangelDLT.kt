package archangeldlt

import archangeldlt.pane.Monitor
import archangeldlt.pane.Search
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import tornadofx.*

class GUI : App(TabBox::class)

fun main(args: Array<String>) {
    launch<GUI>(*args)
}

class TabBox : View("Archangel") {
    val controller = ArchangelController()

    override val root = vbox {
        hbox {
            menubar {
                visibleProperty().bind(controller.ethereum.hasWritePermission())
                menu("SIPs") {
                    item("New SIP").action {
                        controller.createSip()
                    }
                    item("Import Preservica SIP").action {
                        controller.importPreservica()
                    }
                }
            }
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
            vgrow = Priority.ALWAYS
        }
        this.add(controller.notifier)

        prefWidth = 800.0
        prefHeight = 600.0
    }

    override fun onUndock() {
        controller.shutdown()
    }
}

// MenuAction is part of a menubar but is fiddled to act like a button (almost)
class MenuAction(label : String?) : Menu(label) {
    private var actionHandler: () -> Unit = { }

    init {
        val dummy = MenuItem("")
        items.add(dummy)
    }

    override fun show() {
        super.show()
        actionHandler()
    }

    fun action(op: () -> Unit) {
        actionHandler = op
    }

}

fun MenuBar.menuaction(
    name: String? = null, op: Menu.() -> Unit = {}
) = MenuAction(name).also {
    op(it)
    this += it
}


