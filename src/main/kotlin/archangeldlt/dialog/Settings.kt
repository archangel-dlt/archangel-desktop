package dialog

import archangeldlt.ArchangelController
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Settings(controller: ArchangelController) : View("Settings") {
    var endpoint = SimpleStringProperty(controller.endpoint)
    var userAddress = SimpleStringProperty(controller.userAddress)

    override val root = form {
        fieldset {
            field("Ethereum Endpoint") {
                textfield(endpoint)
            }
            field("User Ethereum Address") {
                textfield(userAddress)
            }
        }
        button("Save").action {
            controller.updateSettings(
                endpoint.value,
                userAddress.value
            )
            close()
        }
    }
}