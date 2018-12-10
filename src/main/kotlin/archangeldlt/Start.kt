package archangeldlt

import tornadofx.launch

fun main(args: Array<String>) {
    println("Hello World!")

    launch<GUI>(*args)
}
