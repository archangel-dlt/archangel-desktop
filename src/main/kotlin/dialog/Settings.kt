package dialog

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

data class EthereumDetails(val endpoint: String, val address: String)

class EthereumDetailsModel : ItemViewModel<EthereumDetails>() {
    val KEY_ENDPOINT = "endpoint"
    val KEY_ADDRESS = "address"

    val endpoint = bind { SimpleStringProperty(item?.endpoint, "", app.config.string(KEY_ENDPOINT, "http://localhost:8545")) }
    val address = bind { SimpleStringProperty(item?.address, "", app.config.string(KEY_ADDRESS, "0x0000000000000000000000000000000000000000"))}

    override fun onCommit() {
        with(config) {
            set(KEY_ENDPOINT to endpoint.value)
            set(KEY_ADDRESS to address.value)
            save()
        }
    }
}

class Settings : View("Settings") {
    private val model = EthereumDetailsModel()

    override val root = form {
        fieldset {
            field("Ethereum Endpoint") {
                textfield(model.endpoint)
            }
            field("Your Ethereum Address") {
                textfield(model.address)
            }
        }
        button("Save").action {
            model.commit()
            this@Settings.close()
        }
    }
}