package archangeldlt.pane

import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import archangeldlt.ethereum.Ethereum
import archangeldlt.ethereum.Record
import javafx.collections.FXCollections
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
        searchResults.setResults(results)
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
    private val results = FXCollections.observableArrayList<Record>()

    override val root = tableview(results) {
        column("Block", Record::Block)
        column("Type", Record::Tag)
        column("Key", Record::Key)
        columnResizePolicy = SmartResize.POLICY
    }

    init {
        root.hide()
    }

    fun setResults(newResults : List<Record>) {
        results.clear()
        results.addAll(newResults)

        if (results.isNotEmpty()) root.show() else root.hide()
    }
}