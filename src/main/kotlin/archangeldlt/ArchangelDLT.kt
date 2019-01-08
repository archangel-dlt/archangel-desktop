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
                menuaction("Create SIP").action {
                    controller.createSip()
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
    }

    override fun onUndock() {
        controller.shutdown()
    }
}

// MenuAction is part of a menubar but is fiddled to act like a button
class MenuAction(label : String?) : Menu(label) {
    private var actionHandler: () -> Unit = { }

    init {
        items.add(MenuItem("dummy"))
    }
    override fun show() {
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


