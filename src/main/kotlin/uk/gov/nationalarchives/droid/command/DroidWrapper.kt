package uk.gov.nationalarchives.droid.command

import au.com.bytecode.opencsv.CSVReader
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.RandomStringUtils
import tornadofx.JsonBuilder
import uk.gov.nationalarchives.droid.core.interfaces.config.DroidGlobalProperty
import uk.gov.nationalarchives.droid.core.interfaces.config.RuntimeConfig
import java.io.File
import java.util.*
import javax.json.Json
import javax.json.JsonBuilderFactory
import javax.json.JsonObject
import javax.json.JsonObjectBuilder

class DroidWrapper {
    companion object {
        fun setupDroid() {
            RuntimeConfig.configureRuntimeEnvironment()

            DroidCommandLine.systemExit = false

            ensureSHA256Hash()
        }

        private fun ensureSHA256Hash() {
            val cmdLine = DroidCommandLine(arrayOf())
            val globalContext = cmdLine.context
            val globalConfig = globalContext.globalConfig
            val props = globalConfig.properties

            props.setProperty(DroidGlobalProperty.GENERATE_HASH.name, true)
            props.setProperty(DroidGlobalProperty.HASH_ALGORITHM.name, "sha256")
            props.save()
        }

        fun characterizeFile(paths : List<String>) : List<JsonObject> {
            val passName = uniqueName()
            val profileName = "${passName}.droid"
            val exportName = "${passName}.csv"

            val characteriseArgs = mutableListOf("--open-archives", "--recurse", "--profile(s)", "\"${profileName}\"")
            paths.forEach {
                characteriseArgs.add("--profile-resources")
                characteriseArgs.add("\"${it}\"")
            }

            droid(characteriseArgs)
            droid(listOf("--profile(s)", "\"${profileName}\"", "--export-file", "\"${exportName}\""))

            val csvExport = File(exportName).readText()

            FileUtils.deleteQuietly(File(profileName))
            FileUtils.deleteQuietly(File(exportName))

            return csvToJson(csvExport)
        }

        private fun csvToJson(csv : String) : List<JsonObject> {
            val csvReader = CSVReader(csv.reader(), ',', '"')
            val columnNames = csvReader.readNext().map { it.toString().trim() }
            val desiredColumns = mapOf(
                "URI" to "path",
                "NAME" to "name",
                "SIZE" to "size",
                "TYPE" to "type",
                "LAST_MODIFIED" to "last_modified",
                "SHA256_HASH" to "sha256_hash",
                "PUID" to "puid")

            val fileJson = csvReader.readAll().map { line ->
                    val json = Json.createObjectBuilder()
                    columnNames.mapIndexed { index, key ->
                        if (desiredColumns.containsKey(key) && line[index].isNotBlank()) {
                            json.add(desiredColumns[key], line[index])
                        }
                    }
                    json.build()
            }

            return fileJson
        }

        private fun droid(cmds : List<String>) {
            val cmdLine = DroidCommandLine(cmds.toTypedArray())
            cmdLine.processExecution()
        }

        private fun uniqueName() : String {
            val millis = System.currentTimeMillis()
            var datetime = Date().toGMTString()
            datetime = datetime.replace(" ", "")
            datetime = datetime.replace(":", "")

            val rndchars = RandomStringUtils.randomAlphanumeric(16)
            val filename = "${rndchars }_${datetime}_${millis}"

            return filename
        }
    }
}