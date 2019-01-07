package archangeldlt

import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.Record
import archangeldlt.pane.Monitor
import archangeldlt.pane.Search
import dialog.Settings
import javafx.scene.layout.Priority
import tornadofx.*
import kotlin.reflect.KProperty

class GUI : App(TabBox::class)

fun main(args: Array<String>) {
    launch<GUI>(*args)
}

class ConfigProp(private val key: String, private val defaultValue: String) {
    operator fun getValue(thisRef: ArchangelController, property: KProperty<*>): String {
        return thisRef.app.config.string(key, defaultValue)
    }
    operator fun setValue(thisRef: ArchangelController, property: KProperty<*>, value: String) {
        thisRef.app.config.set(key, value)
        thisRef.app.config.save()
    }
}

class ArchangelController : Controller() {
    val ethereum = Ethereum()
    val events = ethereum.events

    private val KEY_ENDPOINT = "endpoint"
    private val KEY_USERADDRESS = "userAddress"
    private val KEY_WALLETFILE = "walletFile"
    private val KEY_PASSWORD = "password"

    var endpoint: String by ConfigProp(KEY_ENDPOINT, "http://localhost:8545")
    var userAddress: String by ConfigProp(KEY_USERADDRESS, "0x0000000000000000000000000000000000000000")
    var walletFile: String by ConfigProp(KEY_WALLETFILE, "")

    init {
        ethereum.start(endpoint, userAddress)
    }

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

    fun updateSettings(newEndpoint: String, newAddress: String, newWalletFile: String) {
        walletFile = newWalletFile

        if ((newEndpoint == endpoint) && (newAddress == userAddress))
            return
        endpoint = newEndpoint
        userAddress = newAddress

        ethereum.restart(endpoint, userAddress)
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
            vgrow = Priority.ALWAYS
        }
    }

    override fun onUndock() {
        controller.shutdown()
    }
}