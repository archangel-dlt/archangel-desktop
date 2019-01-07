package dialog

import archangeldlt.ArchangelController
import javafx.beans.property.SimpleStringProperty
import javafx.stage.FileChooser
import tornadofx.*

class Settings(controller: ArchangelController) : View("Settings") {
    var endpoint = SimpleStringProperty(controller.endpoint)
    var userAddress = SimpleStringProperty(controller.userAddress)
    var walletFile = SimpleStringProperty(controller.walletFile)

    override val root = form {
        fieldset {
            field("Ethereum Endpoint") {
                textfield(endpoint)
            }
            field("User Ethereum Address") {
                textfield(userAddress)
            }
            field("Wallet file") {
                textfield(walletFile)
                button ("Browse").action {
                    val fileChooser = FileChooser()
                    fileChooser.title = "Wallet file"
                    val wallet = fileChooser.showOpenDialog(controller.primaryStage)
                    walletFile.value = wallet.absolutePath
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