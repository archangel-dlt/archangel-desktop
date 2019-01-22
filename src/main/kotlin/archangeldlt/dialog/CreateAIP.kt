package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import archangeldlt.ethereum.PackageFile
import archangeldlt.ethereum.Record
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class CreateAIP(private val aip: Package,
                private val controller: ArchangelController)
    : View("New AIP") {
    private lateinit var advanceButton: Button

    private val detailsFilled = aip.supplierProperty.isNotEmpty()
        .and(aip.creatorProperty.isNotEmpty())
        .and(aip.rightsProperty.isNotEmpty())
        .and(aip.heldProperty.isNotEmpty())

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
                textfield(aip.supplierProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field("Creator") {
                textfield(aip.creatorProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field()
            field("Rights Statement") {
                textfield(aip.rightsProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field("Held By") {
                textfield(aip.heldProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
        }
        tableview(aip.files) {
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
                action { addFiles() }
            }
            button("Add Directory") {
                visibleProperty().bind(readyToUpload.not())
                prefWidth = 150.0
                action { addDirectory() }
            }
        }

        prefWidth = 900.0
        prefHeight = 500.0
    }

    private fun previous() {
        readyToUpload.value = false
        advanceButton.text = "Create SIP »»"
    }

    private fun advance() {
        if (readyToUpload.value == false) {
            readyToUpload.value = true
            advanceButton.text = "Upload SIP"
        } else {
            uploadSIP()
        }
    }

    private fun addFiles() {
        val fileChooser = FileChooser()
        fileChooser.title = "Add Files To SIP"

        val chosen = fileChooser.showOpenMultipleDialog(controller.primaryStage)

        droidFiles(chosen)
    }

    private fun addDirectory() {
        val dirChooser = DirectoryChooser()
        dirChooser.title = "Add Directory Contents To SIP"

        val chosen = dirChooser.showDialog(controller.primaryStage)

        droidFiles(chosen)
    }

    private fun droidFiles(directory: File) {
        if (directory == null)
            return
        droidFiles(listOf(directory))
    }

    private fun droidFiles(files: List<File>) {
        if (files == null)
            return

        runAsync {
            val fileJson = controller.characterizeFiles(files)
            fileJson.forEach {
                aip.files.add(PackageFile(it))
            }
        }
    }

    private fun uploadSIP() {
        val payload = aip.toJSON()
        controller.store(aip.key, payload)
        close()
    }
}