package archangeldlt

import archangeldlt.dialog.CreateAIP
import archangeldlt.dialog.CreateSIP
import archangeldlt.dialog.ImportPreservica
import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.Record
import archangeldlt.ethereum.Package
import archangeldlt.dialog.Settings
import archangeldlt.ethereum.PackageFile
import archangeldlt.video.VideoUpload
import com.excelmicro.lib.fx.toaster.Notification
import com.excelmicro.lib.fx.toaster.NotificationPane
import org.web3j.crypto.WalletUtils
import tornadofx.Controller
import tornadofx.fail
import tornadofx.finally
import tornadofx.success
import uk.gov.nationalarchives.droid.command.DroidWrapper
import java.io.File
import javax.json.JsonObject


class ArchangelController : Controller() {
    val ethereum = Ethereum()
    val events = ethereum.events
    val conf = ArchangelConfig(app.config)
    val notifier = NotificationPane()

    private val ethMsg = { title: String, msg: String -> this.toast(title, msg) }

    init {
        ethereum.start(conf.endpoint, conf.userAddress, ethMsg)
        DroidWrapper.setupDroid()
    }

    fun shutdown() {
        ethereum.shutdown()
        System.exit(0)
    }

    fun search(searchTerm: String) : List<Record> {
        return ethereum.search(searchTerm)
    }

    fun store(xip: Package, includeFiles: Boolean, label: String) {
        toast(label, "Uploading to Ethereum")

        val payload = xip.toJSON(includeFiles)

        runAsync {
            val creds = WalletUtils.loadCredentials(conf.password, conf.walletFile)
            ethereum.store(xip.key, payload, creds)
        }.success {
            toast(label, "Package written")
            characteriseVideos(xip)
        }.fail {
            toast(label, "Could not write package: ${it.message}")
        }
    }

    fun characteriseVideos(xip: Package) {
        val filesToUpload = xip.toCharacterise()
        if (filesToUpload.size == 0)
            return

        val m = if (filesToUpload.size == 1) { "one file" } else { "${filesToUpload.size} files" }
        toast("Video", "Uploading ${m} for video characterisation ...")

        characteriseVideo(xip.key, filesToUpload, 0)
    }

    fun characteriseVideo(xipKey: String, files: List<PackageFile>, index: Int) {
        if (index == files.size) return

        runAsync {
            VideoUpload(
                xipKey,
                files[index].uuid,
                files[index].fullPath()
            )
        }.success {
            toast("Video", "Uploaded ${files[index].name}")
        }.fail {
            toast("Video", "Upload failed: ${it.message}")
        }.finally {
            characteriseVideo(xipKey, files, index+1)
        }
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

            ethereum.restart(conf.endpoint, conf.userAddress, ethMsg)
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

    fun toast(title: String, msg: String) {
        notifier.notify(Notification(title, msg), 10000)
    }
}

