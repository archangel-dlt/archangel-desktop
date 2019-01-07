package dialog

import archangeldlt.ArchangelController
import javafx.beans.property.SimpleStringProperty
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import java.io.File

class Settings(controller: ArchangelController) : View("Settings") {
    var endpoint = SimpleStringProperty(controller.conf.endpoint)
    var userAddress = SimpleStringProperty(controller.conf.userAddress)
    var walletFile = SimpleStringProperty(controller.conf.walletFile)

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
                    if (wallet != null) walletFile.value = wallet
                }
            }
        }
        button("Save").action {
            controller.updateSettings(
                endpoint.value,
                userAddress.value,
                walletFile.value
            )
            close()
        }
    }
}

fun chooseWalletFile(walletFile: String, stage: Stage): String? {
    val fileChooser = FileChooser()
    fileChooser.title = "Wallet file"

    val f = File(walletFile)
    fileChooser.initialDirectory = f.parentFile
    fileChooser.initialFileName = f.name

    val newWallet = fileChooser.showOpenDialog(stage)
    return newWallet?.absolutePath
}