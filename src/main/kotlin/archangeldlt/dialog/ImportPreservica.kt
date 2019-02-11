package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import javafx.stage.FileChooser
import java.io.File
import java.io.FileReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class ImportPreservica(
    val controller: ArchangelController,
    val sip: Package)
    : CreatePackage(sip, controller, "Preservica SIP") {

    override fun detailsFilled() =
        xip.supplierProperty.isNotEmpty()
        .and(xip.creatorProperty.isNotEmpty())
        .and(xip.rightsProperty.isNotEmpty())
        .and(xip.heldProperty.isNotEmpty())

    companion object {
        fun launch(controller: ArchangelController) {
            val preservica = findPreservicaSip(controller)

            if (preservica == null)
                return

            val sip = loadPreservicaSIP(preservica!!)

            ImportPreservica(controller, sip).openModal()
        } // launch

        fun findPreservicaSip(controller: ArchangelController): File? {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Preservica SIP"

            val chosen = fileChooser.showOpenDialog(controller.primaryStage)
            return chosen
        } // findPreservicaSip

        fun loadPreservicaSIP(sipFile: File): Package {
            val transformer = sipTransformer()

            val importedSip = StringWriter()
            transformer.transform(
                StreamSource(FileReader(sipFile)),
                StreamResult(importedSip)
            )
            println(importedSip)
            return Package.makeSip()
        }

        fun sipTransformer(): Transformer {
            val factory = TransformerFactory.newInstance()
            val xsltResource = javaClass.getResourceAsStream("/preservica-extract.xsl")
            val xsltSource = StreamSource(xsltResource)
            val transformer = factory.newTransformer(xsltSource)
            return transformer
        } // sipTransformer
    }
}