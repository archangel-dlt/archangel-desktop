package archangeldlt.pane

import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.PackageFile
import archangeldlt.ethereum.Record
import com.sun.javafx.scene.control.skin.TableHeaderRow
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import javafx.scene.layout.VBox.setVgrow
import javafx.scene.paint.Color
import tornadofx.*

class Search(ethereum: Ethereum) : View("Search Archangel") {
    private val ethereum = ethereum
    private val searchBox = SearchBox()
    private val searchResults = SearchResults()

    override val root = vbox {
        this@vbox += searchBox
        this@vbox += searchResults
    }

    init {
        searchBox.setOnSearch { searchTerm -> doSearch(searchTerm) }
    }

    private fun doSearch(searchTerm : String) {
        val results = ethereum.search(searchTerm)
        searchResults.setResults(searchTerm, results)
    }
}

class SearchBox : View() {
    private val input = SimpleStringProperty()
    private lateinit var onSearch: (searchTerm: String)->Unit

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

    fun setOnSearch(handler: (searchTerm: String)->Unit) {
        onSearch = handler
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

class SearchResults : View() {
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
                graphic = cache {
                    stackpane {
                        this += SearchResult(it)
                    }
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

class SearchResult(record : Record) : View() {
    override val root = form {
        fieldset {
            field("Citation Reference") {
                textfield(record.citation) {
                    setEditable(false)
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
            readonlyColumn("Type", PackageFile::type)
            readonlyColumn("Puid", PackageFile::puid)
            readonlyColumn("Hash", PackageFile::hash)
            readonlyColumn("Size", PackageFile::size)
            readonlyColumn("Last Modified", PackageFile::lastModified)
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