package archangeldlt

import tornadofx.App

class GUI : App(TabBox::class) {
    val ethereum = Ethereum()
}