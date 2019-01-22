package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import archangeldlt.ethereum.PackageFile
import archangeldlt.ethereum.Record
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class CreateAIP(aip: Package,
                controller: ArchangelController)
    : CreatePackage(aip, controller, "AIP") {

    override fun detailsFilled() = xip.citationProperty.isNotEmpty()
        .and(xip.supplierProperty.isNotEmpty())
        .and(xip.creatorProperty.isNotEmpty())
        .and(xip.rightsProperty.isNotEmpty())
        .and(xip.heldProperty.isNotEmpty())
}