package archangeldlt.ethereum

import archangeldlt.contract.Archangel
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.reactivex.disposables.Disposable
import javafx.collections.FXCollections
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import java.lang.StringBuilder
import java.math.BigInteger

class Ethereum() {
    val events = FXCollections.observableArrayList<Record>()
    private var registrationEventSubscription: Disposable? = null
    private var updateEventsSubscription: Disposable? = null
    private lateinit var web3j: Web3j

    fun start(endpoint: String, userAddress: String) {
        startWeb3(endpoint, userAddress)
    }

    fun restart(endpoint: String, userAddress: String) {
        shutdown()
        startWeb3(endpoint, userAddress)
    }

    fun search(phrase: String) : List<Record> {
        println("Searching for ${phrase}")

        val searchTerm = phrase.toLowerCase()

        val results = events
            .filter { it ->
                matches(it.item.creator, searchTerm) ||
                matches(it.item.supplier, searchTerm) ||
                matches(it.item.held, searchTerm) ||
                matches(it.item.citation, searchTerm) ||
                fileHashMatch(it.item.files, searchTerm)
            }
            .sortedByDescending { it.block }

        return results
    }

    fun shutdown() {
        events.clear()
        web3j.shutdown()
    }

    private fun startWeb3(endpoint: String, userAddress: String) {
        web3j = Web3j.build(HttpService(endpoint))
        val archangelContractAddress = "0xb5ccf2f1d5eb411705d02f59f6b3d694268cfdad"

        println("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().web3ClientVersion)

        val gasProvider = DefaultGasProvider()
        val transactionManager = ReadonlyTransactionManager(web3j, userAddress)
        val archangel = Archangel.load(
            archangelContractAddress,
            web3j,
            transactionManager,
            gasProvider
        )

        val fromBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(2898300))
        val lastBlock = DefaultBlockParameter.valueOf("latest")

        val registrationEvents = archangel.registrationEventFlowable(fromBlock, lastBlock)
        val updateEvents = archangel.updateEventFlowable(fromBlock, lastBlock)

        registrationEventSubscription = registrationEvents.subscribe {
            newEvent("Register", it.log.blockNumber, it._addr, it._key, it._payload)
        }
        updateEventsSubscription = updateEvents.subscribe {
            newEvent("Update", it.log.blockNumber, it._addr, it._key, it._payload)
        }
    }

    private fun newEvent(tag: String, block: BigInteger, addr: String, key: String, bodyStr: String) {
        val jsonParser = Parser()
        val body: JsonObject = jsonParser.parse(StringBuilder(bodyStr)) as JsonObject
        val data = body.obj("data")
        val files = body.array<JsonObject>("files")
        val timestamp = body.string("timestamp")

        if (timestamp == null || data == null || files == null)
            return

        events.add(Record(block, addr, tag, key, timestamp, data, files))
        events.sortByDescending { it.block }
    }

    companion object {
        fun matches(field : String?, searchTerm : String) : Boolean {
            return (field != null) &&
                    (field.toLowerCase().indexOf(searchTerm) != -1)
        }
        fun fileHashMatch(files : List<PackageFile>, searchTerm : String) : Boolean {
            return files.find {
                f -> f.hash.toLowerCase() == searchTerm
            } != null
        }
    }
}