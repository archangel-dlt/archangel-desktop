package archangeldlt.pane

import archangeldlt.ArchangelController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import archangeldlt.ethereum.PackageFile
import archangeldlt.ethereum.Record
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import tornadofx.*

class Search(private val controller: ArchangelController) : View("Search Archangel") {
    private val searchBox = SearchBox { searchTerm -> doSearch(searchTerm) }
    private val searchResults = SearchResults { record -> createAip(record) }

    override val root = vbox {
        this@vbox += searchBox
        this@vbox += searchResults
    }

    private fun doSearch(searchTerm : String) {
        val results = controller.search(searchTerm)
        searchResults.setResults(searchTerm, results)
    }

    private fun createAip(record: Record) {
        controller.createAip(record)
    }
}

class SearchBox(private val onSearch: (searchTerm: String)->Unit) : View() {
    private val input = SimpleStringProperty()

    init {
        resetInput()
    }

    override val root = hbox {
        textfield(input) {
            hboxConstraints {
                hGrow = Priority.ALWAYS
            }
        }
        button("Search") {
            isDefaultButton = true
            action {
                doSearch()
            }
        }
    }

    private fun doSearch() {
        val term = input.value.trim()
        if (term.isEmpty())
            return

        onSearch(term)

        resetInput()
    }

    private fun resetInput() {
        input.value = ""
    }
}

class SearchResults(private val onCreateAip: (sip: Record)->Unit) : View() {
    private var termLabel = SimpleStringProperty()
    private var countLabel = SimpleStringProperty()
    private val results = FXCollections.observableArrayList<Record>()

    override val root = vbox {
        hbox {
            textfield(termLabel) {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                setEditable(false)
            }
            textfield(countLabel) {
                setEditable(false)
            }
        }
        listview(results) {
            cellFormat {
                graphic = stackpane {
                    this += SearchResult(it, onCreateAip)
                }
            }
            vboxConstraints {
                vgrow = Priority.ALWAYS
            }
        }
        vboxConstraints {
            vgrow = Priority.ALWAYS
        }
    }

    fun setResults(term : String, newResults : List<Record>) {
        results.clear()
        results.addAll(newResults)

        termLabel.setValue("Searched for '${term}'")
        countLabel.setValue("${results.size} packages found")
    }
}

class SearchResult(private val record : Record,
                   private val onCreateAip: (sip: Record)->Unit)
    : View() {
    override val root = form {
        hbox {
            textflow {
                text(if (record.isSip) "SIP" else "AIP") {
                    style = "-fx-font-weight: bold"
                }
                text(" - ${record.key}"){
                    style = "-fx-font-weight: normal"
                }
            }
            region {
                hgrow = Priority.SOMETIMES
            }
            if (record.isSip && record.owned) {
                button("Create AIP") {
                    action { onCreateAip(record) }
                    prefWidth = 150.0
                }
            }
        }
        if (record.isAip) {
            fieldset {
                field("Catalogue Reference") {
                    textfield(record.citation) {
                        setEditable(false)
                    }
                }
            }
        }
        fieldset {
            if (record.title.isNotEmpty()) {
                field("Title/Collection") {
                    textfield(record.title) {
                        setEditable(false)
                    }
                }
            }
            if (record.ref.isNotEmpty()) {
                field("Local Reference") {
                    textfield(record.ref) {
                        setEditable(false)
                    }
                }
            }
        }

        fieldset {
            field("Supplier") {
                textfield(record.supplier) {
                    setEditable(false)
                }
            }
            field("Creator") {
                textfield(record.creator) {
                    setEditable(false)
                }
            }
        }
        fieldset {
            field("Rights statement") {
                textfield(record.rights) {
                    setEditable(false)
                }
            }
            field("Held by") {
                textfield(record.held) {
                    setEditable(false)
                }
            }
        }
        tableview(record.files) {
            if (record.hasFilenames) {
                readonlyColumn("Path", PackageFile::path)
                readonlyColumn("Name", PackageFile::name)
            }
            readonlyColumn("Type", PackageFile::type)
            readonlyColumn("PUID", PackageFile::puid)
            readonlyColumn("Size", PackageFile::size)
            readonlyColumn("Last Modified", PackageFile::lastModified)
            readonlyColumn("Checksum", PackageFile::hash)
            if (record.hasUuid) {
                readonlyColumn("File UUID", PackageFile::uuid)
            }
            resizeColumnsToFitContent()
            fixedCellSize = 24.0

            setTableHeightByRowCount(this, record.files)
        }
    }

    companion object {
        fun setTableHeightByRowCount(table : TableView<PackageFile>, files : ObservableList<PackageFile>) {
            val rowCount = files.size
            val tableHeight = (rowCount * table.fixedCellSize) +
                    table.insets.top + table.insets.bottom +
                    50.0
            table.minHeight = tableHeight
            table.maxHeight = tableHeight
            table.prefHeight = tableHeight
        }
    }
}