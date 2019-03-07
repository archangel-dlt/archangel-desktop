package archangeldlt.dialog

import archangeldlt.ArchangelController
import archangeldlt.ethereum.Package
import archangeldlt.ethereum.PackageFile
import javafx.stage.FileChooser
import java.io.File
import java.io.FileReader
import java.io.StringWriter
import java.net.URLDecoder
import javax.json.Json
import javax.json.JsonObject
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class ImportPreservica(
    sip: Package,
    filesToCharacterize: List<File>,
    controller: ArchangelController
) : CreatePackage(sip, controller, "SIP", false) {
    init {
        if (filesToCharacterize.size != 0) {
            val m = if (filesToCharacterize.size == 1) {
                "one file"
            } else {
                "${filesToCharacterize.size} files"
            }
            controller.toast("DROID", "Characterising ${m} ...")

            runAsync {
                val fileJson = controller.characterizeFiles(filesToCharacterize)
                fileJson.forEach {
                    val path = URLDecoder.decode(it.getString("path"), "UTF-8")
                    val original = findPackageFile(path)
                    original?.puid = it.getString("puid", "")
                    if (original?.hash == "")
                        original.hash = it.getString("sha256_hash", "")
                }
                fileTable.refresh()
            }
        }
    }

    fun findPackageFile(path: String): PackageFile? {
        val original = xip.files.find { path.endsWith(it.fullPath()) }
        return original
    }

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
            if (sip == null) {
                controller.toast("Preservica", "Doesn't look like a Preservica SIP")
                return
            }

            val filesToCharacterise = filesToDroid(sip, preservica)

            ImportPreservica(sip, filesToCharacterise, controller).openModal()
        } // launch

        fun findPreservicaSip(controller: ArchangelController): File? {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Preservica SIP"

            val chosen = fileChooser.showOpenDialog(controller.primaryStage)
            return chosen
        } // findPreservicaSip

        fun loadPreservicaSIP(sipFile: File): Package? {
            try {
                val sipString = readSipFile(sipFile)

                val sipJson = sipStringToJson(sipString)

                val data = sipJson.getJsonObject("data")
                var files = sipJson.getJsonArray("files")
                return Package.fromEvent(
                    data.getString("key"),
                    data,
                    files
                )
            } catch (e: Exception) {
                return null
            }
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

        fun filesToDroid(sip: Package, sipFile: File): List<File> {
            val rootDir = sipFile.parent

            val fileNames = sip.files.map { "${rootDir}${File.separator}${it.fullPath()}"}
            val files = fileNames.map { File(it) }.filter { it.exists() }
            return files
        }
    }
}