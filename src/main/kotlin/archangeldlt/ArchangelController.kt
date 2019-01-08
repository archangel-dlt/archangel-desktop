package archangeldlt

import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.Record
import dialog.Settings
import tornadofx.Controller

class ArchangelController : Controller() {
    val ethereum = Ethereum()
    val events = ethereum.events
    val conf = ArchangelConfig(app.config)

    init {
        ethereum.start(conf.endpoint, conf.userAddress)
    }

    fun shutdown() {
        ethereum.shutdown()
    }

    fun search(searchTerm: String) : List<Record> {
        return ethereum.search(searchTerm)
    }

    fun openSettings() {
        Settings(this).openModal()
    }

    fun updateSettings(newEndpoint: String,
                       newAddress: String,
                       newWalletFile: String,
                       newPassword: String) {
        if (conf.walletFile != newWalletFile) {
            conf.walletFile = newWalletFile
            conf.password = newPassword
        }

        if (conf.endpoint != newEndpoint || conf.userAddress != newAddress) {
            conf.endpoint = newEndpoint
            conf.userAddress = newAddress

            ethereum.restart(conf.endpoint, conf.userAddress)
        }
    }
}

