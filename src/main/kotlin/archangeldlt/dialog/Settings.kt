package dialog

import archangeldlt.ArchangelController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.control.TextInputDialog
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.web3j.crypto.WalletUtils
import tornadofx.*
import java.io.File

class Settings(controller: ArchangelController) : View("Settings") {
    var endpoint = SimpleStringProperty(controller.conf.endpoint)
    var userAddress = SimpleStringProperty(controller.conf.userAddress)
    var walletFile = SimpleStringProperty(controller.conf.walletFile)
    var password = ""

    override val root = form {
        fieldset {
            field("Ethereum Endpoint") {
                textfield(endpoint)
            }
            field("User Ethereum Address") {
                textfield(userAddress)
            }
            field("Wallet file") {
                textfield(walletFile) {
                    setEditable(false)
                }
                button ("Browse").action {
                    val wallet = chooseWalletFile(walletFile.value, controller.primaryStage)
                    if (wallet != null) {
                        walletFile.value = wallet.file
                        userAddress.value = wallet.address
                        password = wallet.password
                    }
                }
            }
        }
        button("Save").action {
            controller.updateSettings(
                endpoint.value,
                userAddress.value,
                walletFile.value,
                password
            )
            close()
        }
    }
}

data class WalletDetails(val file: String,
                         val password: String,
                         val address: String)

fun chooseWalletFile(walletFile: String, stage: Stage): WalletDetails? {
    val fileChooser = FileChooser()
    fileChooser.title = "Wallet file"

    val f = File(walletFile)
    fileChooser.initialDirectory = f.parentFile
    fileChooser.initialFileName = f.name

    try {
        val newWallet = fileChooser.showOpenDialog(stage)
        if (newWallet == null) return null

        val password = solicitPassword()
        if (password == null) return null

        val creds = WalletUtils.loadCredentials(password, newWallet as File)
        return WalletDetails(
            newWallet?.absolutePath,
            password,
            creds.address
        )
    } catch (e: Exception) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.headerText = "Wallet Error"
        alert.contentText = e.message
        alert.showAndWait()
    }
    return null
}

fun solicitPassword(): String? {
    val passwordBox = TextInputDialog("")
    passwordBox.title = null
    passwordBox.headerText = null
    passwordBox.contentText = "Wallet password"

    val result = passwordBox.showAndWait()
    return result.orElse(null)
}
