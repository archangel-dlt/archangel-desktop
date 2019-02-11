package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import javafx.stage.FileChooser
import java.io.File
import java.io.FileReader
import java.io.StringWriter
import javax.json.Json
import javax.json.JsonObject
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class ImportPreservica(
    sip: Package,
    controller: ArchangelController
) : CreatePackage(sip, controller, "SIP", false) {
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

            val sip = loadPreservicaSIP(preservica)

            ImportPreservica(sip, controller).openModal()
        } // launch

        fun findPreservicaSip(controller: ArchangelController): File? {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Preservica SIP"

            val chosen = fileChooser.showOpenDialog(controller.primaryStage)
            return chosen
        } // findPreservicaSip

        fun loadPreservicaSIP(sipFile: File): Package {
            val sipString = readSipFile(sipFile)

            val sipJson = sipStringToJson(sipString)

            val data = sipJson.getJsonObject("data")
            var files = sipJson.getJsonArray("files")
            return Package.fromEvent(
                data.getString("key"),
                data,
                files
            )
        } // loadPreservicaSIP

        fun sipStringToJson(sipString: String): JsonObject {
            val reader = Json.createReader(sipString.reader())
            val json = reader.readObject()
            reader.close()
            return json
        }

        fun readSipFile(sipFile: File): String {
            val transformer = sipTransformer()

            val importedSip = StringWriter()
            transformer.transform(
                StreamSource(FileReader(sipFile)),
                StreamResult(importedSip)
            )

            return importedSip.toString()
        } // readSipString

        fun sipTransformer(): Transformer {
            val factory = TransformerFactory.newInstance()
            val xsltResource = ImportPreservica::class.java.getResourceAsStream("/preservica-extract.xsl")
            val xsltSource = StreamSource(xsltResource)
            val transformer = factory.newTransformer(xsltSource)
            return transformer
        } // sipTransformer
    }
}