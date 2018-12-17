package archangeldlt.pane

import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import archangeldlt.ethereum.Ethereum
import tornadofx.*

class Search(ethereum: Ethereum) : View("Search Archangel") {
    val input = SimpleStringProperty()

    override val root = vbox {
        hbox {
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
    }

    fun doSearch() {
        val term = input.value.trim()
        if (term.isEmpty())
            return

        searchEvents(term)
        input.value = ""
    }

    fun searchEvents (searchTerm: String) {
        println("Searching for ${searchTerm}")
    }
}