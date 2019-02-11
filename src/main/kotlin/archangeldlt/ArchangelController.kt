package archangeldlt

import archangeldlt.dialog.CreateAIP
import archangeldlt.dialog.CreateSIP
import archangeldlt.dialog.ImportPreservica
import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.Record
import archangeldlt.ethereum.Package
import archangeldlt.dialog.Settings
import org.web3j.crypto.WalletUtils
import tornadofx.Controller
import uk.gov.nationalarchives.droid.command.DroidWrapper
import java.io.File
import javax.json.JsonObject

class ArchangelController : Controller() {
    val ethereum = Ethereum()
    val events = ethereum.events
    val conf = ArchangelConfig(app.config)

    init {
        ethereum.start(conf.endpoint, conf.userAddress)
        DroidWrapper.setupDroid()
    }

    fun shutdown() {
        ethereum.shutdown()
    }

    fun search(searchTerm: String) : List<Record> {
        return ethereum.search(searchTerm)
    }

    fun store(key: String, payload: JsonObject) {
        val creds = WalletUtils.loadCredentials(conf.password, conf.walletFile)
        ethereum.store(key, payload, creds)
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

    fun createSip() {
        CreateSIP(this).openModal()
    }
    fun importPreservica() {
        ImportPreservica.launch(this)
    }

    fun createAip(record: Record) {
        val aip = Package.makeAip(record.info)
        CreateAIP(aip,this).openModal()
    }

    fun characterizeFiles(files : List<File>) : List<JsonObject> {
        val fileNames = files.map { it->it.absolutePath }

        return DroidWrapper.characterizeFile(fileNames)
    }
}

