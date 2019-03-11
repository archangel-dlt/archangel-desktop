package archangeldlt

import tornadofx.ConfigProperties
import kotlin.reflect.KProperty

class ConfigProp(private val key: String, private val defaultValue: String) {
    operator fun getValue(thisRef: ArchangelConfig, property: KProperty<*>): String {
        return thisRef.config.string(key, defaultValue)
    }
    operator fun setValue(thisRef: ArchangelConfig, property: KProperty<*>, value: String) {
        thisRef.config.set(key, value)
        thisRef.config.save()
    }
}

class ArchangelConfig(val config: ConfigProperties) {
    private val KEY_ENDPOINT = "endpoints"
    private val KEY_USERADDRESS = "userAddress"
    private val KEY_WALLETFILE = "walletFile"
    private val KEY_PASSWORD = "password"

    var endpoint: String by ConfigProp(KEY_ENDPOINT, "https://blockchain.surrey.ac.uk/ethereum/")
    var userAddress: String by ConfigProp(KEY_USERADDRESS, "0x0000000000000000000000000000000000000000")
    var walletFile: String by ConfigProp(KEY_WALLETFILE, "")
    var password: String by ConfigProp(KEY_PASSWORD, "")
}

