package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import archangeldlt.ethereum.PackageFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class CreateSIP(val controller: ArchangelController)
    : CreatePackage(Package.makeSip(), controller, "SIP") {

    override fun detailsFilled() = xip.supplierProperty.isNotEmpty()
        .and(xip.creatorProperty.isNotEmpty())
        .and(xip.rightsProperty.isNotEmpty())
        .and(xip.heldProperty.isNotEmpty())
}