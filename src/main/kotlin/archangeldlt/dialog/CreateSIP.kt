package archangeldlt.dialog

import archangeldlt.ArchangelController
import com.beust.klaxon.JsonObject
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class Sip : JsonModel {
    val key = UUID.randomUUID().toString()
    val supplierProperty = SimpleStringProperty()
    val supplier by supplierProperty
    val creatorProperty = SimpleStringProperty()
    val creator by creatorProperty
    val rightsStatementProperty = SimpleStringProperty()
    val rightsStatement by rightsStatementProperty
    val heldByProperty = SimpleStringProperty()
    val heldBy by heldByProperty

    val detailsFilled = supplierProperty.isNotEmpty()
        .and(creatorProperty.isNotEmpty())
        .and(rightsStatementProperty.isNotEmpty())
        .and(heldByProperty.isNotEmpty())

    override fun toJSON(json: JsonBuilder) {
        val data = JsonBuilder()
        with (data) {
            add("key", key)
            add("pack", "sip")
            add("supplier", supplier)
            add("creator", creator)
            add("rights", rightsStatement)
            add("held", heldBy)
        }

        with (json) {
            add("data", data)
            add("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        }
    }
}

class CreateSIP(val controller: ArchangelController) : View("New SIP") {
    private lateinit var advanceButton: Button

    private val sip = Sip()
    private val readyToUpload = SimpleBooleanProperty(false)

    override val root = form {
        hbox {
            button("«« Back") {
                visibleProperty().bind(readyToUpload)
                action { previous() }
                prefWidth = 150.0
            }
            region {
                hgrow = Priority.SOMETIMES
            }
            button("Create SIP »»") {
                disableProperty().bind(sip.detailsFilled.not())
                action { advance() }
                prefWidth = 150.0
                advanceButton = this
            }
        }
        fieldset {
            field("Supplier") {
                textfield(sip.supplierProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field("Creator") {
                textfield(sip.creatorProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field()
            field("Rights Statement") {
                textfield(sip.rightsStatementProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field("Held By") {
                textfield(sip.heldByProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
        }

        prefWidth = 900.0
        prefHeight = 500.0
    }

    fun previous() {
        readyToUpload.value = false
        advanceButton.text = "Create SIP »»"
    }

    fun advance() {
        if (readyToUpload.value == false) {
            readyToUpload.value = true
            advanceButton.text = "Upload SIP"
        } else {
            uploadSIP()
        }
    }

    fun uploadSIP() {
        val payload = sip.toJSON()
        controller.store(sip.key, payload)
        close()
    }
}