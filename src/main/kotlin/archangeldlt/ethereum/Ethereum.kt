package archangeldlt.ethereum

import archangeldlt.contract.Archangel
import io.reactivex.disposables.Disposable
import javafx.collections.FXCollections
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

class Ethereum {
    val events = FXCollections.observableArrayList<Record>()
    private var registrationEventSubscription: Disposable? = null
    private var updateEventsSubscription: Disposable? = null
    private val web3j = Web3j.build(HttpService("http://localhost:8545"))

    init {
        startWeb3()
    }

    private fun startWeb3() {
        val userAddress = "0x0000000000000000000000000000000000000000"
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

        registrationEventSubscription = registrationEvents.subscribe(
            {
                newEvent("Register", it._key, it._payload)
            }
        )
        updateEventsSubscription = updateEvents.subscribe(
            {
                newEvent("Update", it._key, it._payload)
            }
        )
    }

    private fun newEvent(tag: String, key: String, bodyStr: String) {
        events.add(Record(tag))
    }
}