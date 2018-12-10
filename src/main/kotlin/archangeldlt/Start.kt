package archangeldlt

import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import archangeldlt.contract.Archangel
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.tx.ReadonlyTransactionManager
import java.math.BigInteger

fun main(args: Array<String>) {
    println("Hello World!")

    val userAddress = "0xF5F5F860C6d60C28e808c27708FD13FD96596752"
    val archangelContractAddress = "0xb5ccf2f1d5eb411705d02f59f6b3d694268cfdad"

    val web3j = Web3j.build(HttpService("http://localhost:8545"))
    try {
        println("Connected to Etherum client version: " + web3j.web3ClientVersion().send().web3ClientVersion)

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

        updateEvents.subscribe(
            {
                println("Update  : ${it._key} ${it._payload}")
            }
        )
        registrationEvents.subscribe(
            {
                println("Register: ${it._key} ${it._payload}")
            }
        )
    }
    finally {
        web3j.shutdown()
    }
}