package archangeldlt

import archangeldlt.contract.Archangel
import archangeldlt.pane.Search
import io.reactivex.disposables.Disposable
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import tornadofx.App
import java.math.BigInteger

class GUI : App(Search::class) {
    var registrationEventSubscription: Disposable? = null
    var updateEventsSubscription: Disposable? = null
    val web3j = Web3j.build(HttpService("http://localhost:8545"))

    init {
        startWeb3()
    }


    private fun startWeb3() {
        val userAddress = "0xF5F5F860C6d60C28e808c27708FD13FD96596752"
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

        registrationEventSubscription = updateEvents.subscribe(
            {
                println("Update  : ${it._key} ${it._payload}")
            }
        )
        updateEventsSubscription = registrationEvents.subscribe(
            {
                println("Register: ${it._key} ${it._payload}")
            }
        )
    }
}