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
import java.math.BigInteger
import org.web3j.tx.gas.StaticGasProvider
import javax.json.Json
import javax.json.JsonValue

data class NetworkDetails(
    val id: String,
    val name: String,
    val fromBlock: Int,
    val gasLimit: BigInteger,
    val gasPrice: BigInteger
)
val Rinkeby = NetworkDetails(
    "4",
    "Rinkeby",
    80380,
    BigInteger.valueOf(7000000),
    BigInteger.valueOf(10_000_000_000L)
)
val ArchangelDev = NetworkDetails(
    "3151",
    "Archangel Dev",
    1,
    BigInteger.valueOf(75_000_000L),
    BigInteger.valueOf(22_000_000_000L)  // web3 default
)
val ArchangelUser = NetworkDetails(
    "53419",
    "Archangel User Study",
    1,
    BigInteger.valueOf(83_886_080L),
    BigInteger.valueOf(22_000_000_000L)  // web3 default
)

class Ethereum() {
    val events = FXCollections.observableArrayList<Record>()
    private var registrationEventSubscription: Disposable? = null
    private var updateEventsSubscription: Disposable? = null
    private lateinit var web3j: Web3j
    private lateinit var userAddress: String
    private lateinit var archangelContractAddress: String
    private lateinit var networkId: String
    private val writePermission = SimpleBooleanProperty(false)

    private val networks = mapOf(
        Rinkeby.id to Rinkeby,
        ArchangelDev.id to ArchangelDev,
        ArchangelUser.id to ArchangelUser
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
        this.userAddress = userAddress

        networkId = web3j.netVersion().send().result
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
        if (networks.containsKey(networkId))
            return networks[networkId]!!.name
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

        val owned = (addr.toLowerCase() == userAddress.toLowerCase())
        events.add(Record(block, addr, tag, key, timestamp, data, files, owned))
        events.sortByDescending { it.block }
    }

    private fun eventToJson(bodyStr: String) : javax.json.JsonObject {
        val reader = Json.createReader(bodyStr.reader())
        val json = reader.readObject()
        reader.close()
        return json
    }

    private fun loadContract (transactionManager: TransactionManager) : Archangel {
        val network = networks[networkId]!!
        val gasProvider = StaticGasProvider(
            network.gasPrice,
            network.gasLimit
        )

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