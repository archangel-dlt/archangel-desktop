package archangeldlt

import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

fun main(args: Array<String>) {
    println("Hello World!")

    val web3j = Web3j.build(HttpService("http://localhost:8545"))
    println("Connected to Etherum client version: " + web3j.web3ClientVersion().send().web3ClientVersion)
}