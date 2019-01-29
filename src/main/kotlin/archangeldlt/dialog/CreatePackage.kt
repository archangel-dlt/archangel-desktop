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
                    private val label: String)
    : View("New ${label}") {
    private lateinit var advanceButton: Button
    private lateinit var fileTable: TableView<PackageFile>

    protected open fun detailsFilled() : BooleanBinding = SimpleBooleanProperty(true).toBinding()
    protected val readyToUpload = SimpleBooleanProperty(false)
    protected val includeFiles = SimpleBooleanProperty(true)

    protected val includeFilesToggle = CheckBox()

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
                field("Citation Reference") {
                    textfield(xip.citationProperty) {
                        disableProperty().bind(readyToUpload)
                    }
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
            readonlyColumn("Puid", PackageFile::puid)
            readonlyColumn("Hash", PackageFile::hash)
            readonlyColumn("Size", PackageFile::size)
            readonlyColumn("Last Modified", PackageFile::lastModified)

            columns[1].graphic = includeFilesToggle
            columns[0].visibleProperty().bind(includeFiles.or(readyToUpload.not()))
            columns[1].visibleProperty().bind(includeFiles.or(readyToUpload.not()))
            columnResizePolicy = SmartResize.POLICY
            vgrow = Priority.ALWAYS
            fileTable = this
        }
        if (xip.isSip) {
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
        }

        prefWidth = 900.0
        prefHeight = 500.0
    }

    private fun previous() {
        readyToUpload.value = false
        advanceButton.text = "Create ${label} »»"
        SmartResize.POLICY.requestResize(fileTable)
    }

    private fun advance() {
        if (readyToUpload.value == false) {
            readyToUpload.value = true
            advanceButton.text = "Upload ${label}"
        } else {
            upload()
        }
    }

    private fun addFiles() {
        val fileChooser = FileChooser()
        fileChooser.title = "Add Files To ${label}"

        val chosen = fileChooser.showOpenMultipleDialog(controller.primaryStage)

        droidFiles(chosen)
    }

    private fun addDirectory() {
        val dirChooser = DirectoryChooser()
        dirChooser.title = "Add Directory Contents To ${label}"

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
                xip.files.add(PackageFile(it))
            }
        }
    }

    private fun upload() {
        val payload = xip.toJSON(includeFiles.value)
        controller.store(xip.key, payload)
        close()
    }
}