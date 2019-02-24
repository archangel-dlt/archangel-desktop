package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import archangeldlt.ethereum.PackageFile
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

open class CreatePackage(protected val xip: Package,
                    private val controller: ArchangelController,
                    private val label: String,
                    private val canAddFiles: Boolean = true)
    : View("New ${label}") {
    private lateinit var advanceButton: Button
    protected lateinit var fileTable: TableView<PackageFile>

    protected open fun detailsFilled() : BooleanBinding = SimpleBooleanProperty(true).toBinding()
    private val readyToUpload = SimpleBooleanProperty(false)
    private val includeFiles = SimpleBooleanProperty(true)

    private val includeFilesToggle = CheckBox()

    init {
        includeFilesToggle.bind(includeFiles)
        includeFilesToggle.visibleProperty().bind(readyToUpload.not())
        if (xip.isAip) {
            includeFilesToggle.disableProperty().value = true
        }
    }

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
            button("Create ${label} »»") {
                disableProperty().bind(detailsFilled().not())
                action { advance() }
                prefWidth = 150.0
                advanceButton = this
            }
        }
        if (xip.isAip) {
            fieldset {
                field("Catalogue Reference") {
                    textfield(xip.citationProperty) {
                        disableProperty().bind(readyToUpload)
                    }
                }
            }
        }
        textflow {
            text(if (xip.isSip) "SIP" else "AIP") {
                style = "-fx-font-weight: bold"
            }
            text(" - ${xip.key}"){
                style = "-fx-font-weight: normal"
            }
        }
        fieldset {
            field("Title/Collection") {
                textfield(xip.titleProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
            field("Local Reference") {
                textfield(xip.refProperty) {
                    disableProperty().bind(readyToUpload)
                }
            }
        }

        fieldset {
            field("Supplier") {
                textfield(xip.supplierProperty) {
                    disableProperty().bind(readyToUpload.or(xip.isAip))
                }
            }
            field("Creator") {
                textfield(xip.creatorProperty) {
                    disableProperty().bind(readyToUpload.or(xip.isAip))
                }
            }
            field()
            field("Rights Statement") {
                textfield(xip.rightsProperty) {
                    disableProperty().bind(readyToUpload.or(xip.isAip))
                }
            }
            field("Held By") {
                textfield(xip.heldProperty) {
                    disableProperty().bind(readyToUpload.or(xip.isAip))
                }
            }
        }
        tableview(xip.files) {
            readonlyColumn("Path", PackageFile::path)
            readonlyColumn("File name", PackageFile::name)
            readonlyColumn("Type", PackageFile::type)
            readonlyColumn("PUID", PackageFile::puid)
            readonlyColumn("Size", PackageFile::size)
            readonlyColumn("Last Modified", PackageFile::lastModified)
            readonlyColumn("Checksum", PackageFile::hash)
            readonlyColumn("File UUID", PackageFile::uuid)

            columns[1].graphic = includeFilesToggle
            resizeColumnsToFitContent()
            vgrow = Priority.ALWAYS
            fileTable = this
        }
        if (xip.isSip && canAddFiles) {
            hbox {
                region {
                    hgrow = Priority.SOMETIMES
                }
                button("Add Files") {
                    visibleProperty().bind(readyToUpload.not())
                    prefWidth = 150.0
                    action { addFiles() }
                }
                button("Add Folder") {
                    visibleProperty().bind(readyToUpload.not())
                    prefWidth = 150.0
                    action { addDirectory() }
                }
            }
        }

        prefWidth = 900.0
        prefHeight = 500.0
    }

    private fun previous() {
        fileTable.columns[0].visibleProperty().value = true
        fileTable.columns[1].visibleProperty().value = true

        readyToUpload.value = false
        advanceButton.text = "Create ${label} »»"
        SmartResize.POLICY.requestResize(fileTable)
    }

    private fun advance() {
        if (readyToUpload.value == false) {
            readyToUpload.value = true
            advanceButton.text = "Upload ${label}"

            fileTable.columns[0].visibleProperty().value = includeFiles.value
            fileTable.columns[1].visibleProperty().value = includeFiles.value
        } else {
            upload()
        }
    }

    private fun addFiles() {
        val fileChooser = FileChooser()
        fileChooser.title = "Add Files To ${label}"

        val chosen = fileChooser.showOpenMultipleDialog(controller.primaryStage)
        if (chosen == null)
            return

        val m = if (chosen.size == 1) { "one file" } else { "${chosen.size} files" }
        controller.toast("DROID", "Characterising ${m} ...")

        droidFiles(chosen)
    }

    private fun addDirectory() {
        val dirChooser = DirectoryChooser()
        dirChooser.title = "Add Folder Contents To ${label}"

        val chosen = dirChooser.showDialog(controller.primaryStage)
        if (chosen == null)
            return

        controller.toast("DROID", "Characterising folder ...")

        droidFiles(chosen)
    }

    private fun droidFiles(directory: File) {
        droidFiles(listOf(directory))
    }

    protected fun droidFiles(files: List<File>) {
        runAsync {
            val fileJson = controller.characterizeFiles(files)
            fileJson.forEach {
                xip.files.add(PackageFile(it))
            }
        }
    }

    private fun upload() {
        controller.store(xip, includeFiles.value, label)
        close()
    }
}