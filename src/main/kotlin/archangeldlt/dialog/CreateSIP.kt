package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import archangeldlt.ethereum.PackageFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import tornadofx.*

class CreateSIP(val controller: ArchangelController) : View("New SIP") {
    private lateinit var advanceButton: Button

    private val sip = Package()
    private val detailsFilled = sip.supplierProperty.isNotEmpty()
        .and(sip.creatorProperty.isNotEmpty())
        .and(sip.rightsProperty.isNotEmpty())
        .and(sip.heldProperty.isNotEmpty())


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
                disableProperty().bind(detailsFilled.not())
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
                textfield(sip.rightsProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field("Held By") {
                textfield(sip.heldProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
        }
        tableview(sip.files) {
            readonlyColumn("Path", PackageFile::path)
            readonlyColumn("File name", PackageFile::name)
            readonlyColumn("Type", PackageFile::type)
            readonlyColumn("Puid", PackageFile::puid)
            readonlyColumn("Hash", PackageFile::hash)
            readonlyColumn("Size", PackageFile::size)
            readonlyColumn("Last Modified", PackageFile::lastModified)

            columns[0].visibleProperty().bind(readyToUpload.not())
            columns[1].visibleProperty().bind(readyToUpload.not())
            columnResizePolicy = SmartResize.POLICY
            vgrow = Priority.ALWAYS
        }
        hbox {
            region {
                hgrow = Priority.SOMETIMES
            }
            button("Add Files") {
                visibleProperty().bind(readyToUpload.not())
                prefWidth = 150.0
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