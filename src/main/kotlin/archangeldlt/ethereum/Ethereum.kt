package archangeldlt.ethereum

import archangeldlt.contract.Archangel
import io.reactivex.disposables.Disposable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import kotlinx.coroutines.*
import javax.json.Json
import javax.json.JsonValue

class Ethereum() {
    val events = FXCollections.observableArrayList<Record>()
    private var registrationEventSubscription: Disposable? = null
    private var updateEventsSubscription: Disposable? = null
    private lateinit var web3j: Web3j
    private lateinit var archangelContractAddress: String
    private val writePermission = SimpleBooleanProperty(false)

    private val networkNames = mapOf(
        "4" to "Rinkeby",
        "3151" to "Archangel-Dev",
        "53419" to "Archangel User Study"
    )

    fun start(
        endpoint: String,
        userAddress: String,
        callback: (String) -> Unit
    ) {
        startWeb3(endpoint, userAddress, callback)
    }

    fun restart(
        endpoint: String,
        userAddress: String,
        callback: (String) -> Unit
    ) {
        shutdown()
        startWeb3(endpoint, userAddress, callback)
    }

    fun search(phrase: String) : List<Record> {
        println("Searching for ${phrase}")

        val searchTerm = phrase.toLowerCase()

        val results = events
            .filter {
                matches(it.title, searchTerm) ||
                matches(it.creator, searchTerm) ||
                matches(it.supplier, searchTerm) ||
                matches(it.held, searchTerm) ||
                matches(it.citation, searchTerm) ||
                fileMatch(it.files, searchTerm)
            }
            .sortedByDescending { it.block }

        return results
    }

    fun shutdown() {
        events.clear()
        web3j.shutdown()
    }

    fun hasWritePermission() : BooleanProperty {
        return writePermission
    }

    fun store(key: String, payload: javax.json.JsonObject, creds: Credentials) {
        val writeableArchangel = loadContract(RawTransactionManager(web3j, creds))

        val txReceipt = writeableArchangel.store(
            key,
            payload.toString()
        ).send()
    }

    private fun startWeb3(
        endpoint: String,
        userAddress: String,
        callback: (String) -> Unit
    ) {
        web3j = Web3j.build(HttpService(endpoint))

        val networkId = web3j.netVersion().send().result
        val networkName = findNetworkName(networkId)
        archangelContractAddress = Archangel.getPreviouslyDeployedAddress(networkId)

        callback("Connected to ${networkName} network")

        val archangel = loadContract(ReadonlyTransactionManager(web3j, userAddress))

        val fromBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(1))
        val lastBlock = DefaultBlockParameter.valueOf("latest")

        val registrationEvents = archangel.registrationEventFlowable(fromBlock, lastBlock)
        val updateEvents = archangel.updateEventFlowable(fromBlock, lastBlock)

        registrationEventSubscription = registrationEvents.subscribe {
            newEvent("Register", it.log.blockNumber, it._addr, it._key, it._payload)
        }
        updateEventsSubscription = updateEvents.subscribe {
            newEvent("Update", it.log.blockNumber, it._addr, it._key, it._payload)
        }

        writePermission.value = archangel.hasPermission(userAddress).send()
    }

    private fun findNetworkName(networkId: String): String {
        if (networkNames.containsKey(networkId))
            return networkNames[networkId]!!
        return "Unknown"
    }

    private fun newEvent(tag: String, block: BigInteger, addr: String, key: String, bodyStr: String) {
        val body = eventToJson(bodyStr)
        val data = body.getJsonObject("data")
        var files = body.getJsonArray("files")
        val timestamp = body.getString("timestamp")

        if (timestamp == null || data == null)
            return

        if (files == null)
            files = JsonValue.EMPTY_JSON_ARRAY

        events.add(Record(block, addr, tag, key, timestamp, data, files))
        events.sortByDescending { it.block }
    }

    private fun eventToJson(bodyStr: String) : javax.json.JsonObject {
        val reader = Json.createReader(bodyStr.reader())
        val json = reader.readObject()
        reader.close()
        return json
    }

    private fun loadContract (transactionManager: TransactionManager) : Archangel {
        val gasProvider = DefaultGasProvider()

        return Archangel.load(
            archangelContractAddress,
            web3j,
            transactionManager,
            gasProvider
        )
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
        fun fileUuidMatch(files : List<PackageFile>, searchTerm : String) : Boolean {
            return files.find {
                    f -> f.uuid.toLowerCase() == searchTerm
            } != null
        }
        fun fileNameMatch(files : List<PackageFile>, searchTerm : String) : Boolean {
            return files.find {
                    f -> f.name.toLowerCase().indexOf(searchTerm) != -1
            } != null
        }

        fun fileMatch(files: List<PackageFile>, searchTerm: String) : Boolean {
            return fileHashMatch(files, searchTerm) ||
                    fileUuidMatch(files, searchTerm) ||
                    fileNameMatch(files, searchTerm)
        }
    }
}