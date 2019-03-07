package archangeldlt.ethereum

import archangeldlt.video.formats
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*
import java.io.File
import java.net.URLDecoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.json.*

class Package {
    val titleProperty = SimpleStringProperty()
    val refProperty = SimpleStringProperty()
    val citationProperty = SimpleStringProperty()
    val supplierProperty = SimpleStringProperty()
    val creatorProperty = SimpleStringProperty()
    val rightsProperty = SimpleStringProperty()
    val heldProperty = SimpleStringProperty()

    var key : String = ""
    val title by titleProperty
    val ref by refProperty
    val citation by citationProperty
    val supplier by supplierProperty
    val creator by creatorProperty
    val rights by rightsProperty
    val held by heldProperty
    val isSip: Boolean
    val isAip: Boolean

    val files = FXCollections.observableArrayList<PackageFile>()

    private constructor() {
        key = UUID.randomUUID().toString()
        isSip = true
        isAip = false
    }

    private constructor(sip: Package) {
        key = sip.key
        titleProperty.value = sip.titleProperty.value
        refProperty.value = sip.refProperty.value
        citationProperty.value = sip.citationProperty.value
        supplierProperty.value = sip.supplierProperty.value
        creatorProperty.value = sip.creatorProperty.value
        rightsProperty.value = sip.rightsProperty.value
        heldProperty.value = sip.heldProperty.value
        isSip = false
        isAip = true

        files.addAll(sip.files)
    }

    private constructor(eventKey: String, data : JsonObject, fileList : JsonArray) {
        key = eventKey
        titleProperty.value = data.getString("collection", "")
        refProperty.value = data.getString("ref", "")
        citationProperty.value = data.getString("citation", "")
        supplierProperty.value = data.getString("supplier", "")
        creatorProperty.value = data.getString("creator", "")
        rightsProperty.value = data.getString("rights", "")
        heldProperty.value = data.getString("held", "")
        isSip = data.getString("pack", "") == "sip"
        isAip = data.getString("pack", "") == "aip"

        fileList.forEach {
            val file = PackageFile(it.asJsonObject())
            files.add(file)
        }
    }

    fun hasFilenames() : Boolean {
        return files.any {
            it.path.isNotEmpty() || it.name.isNotEmpty()
        }
    }
    fun hasUuid()  : Boolean {
        return files.any {
            it.uuid.isNotEmpty()
        }
    }

    fun toJSON(includeFilenames: Boolean) : JsonObject {
        val data = JsonBuilder()
        with (data) {
            add("key", key)
            add("pack", if (isAip) { "aip" } else { "sip" })
            add("collection", title)
            add("ref", ref)
            add("citation", citation)
            add("supplier", supplier)
            add("creator", creator)
            add("rights", rights)
            add("held", held)
        }

        val fileJson = files.map { it.toJson(includeFilenames || isAip) }

        val json = JsonBuilder()
        with (json) {
            add("data", data)
            add("files", fileJson)
            add("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        }
        return json.build()
    }

    fun toCharacterise() : List<PackageFile> {
        return files.filter {
            formats.indexOf(it.puid) != -1
        }
    }

    companion object {
        fun makeSip() : Package {
            return Package()
        }
        fun makeAip(sip: Package) : Package {
            return Package(sip)
        }
        fun fromEvent(eventKey: String, data : JsonObject, fileList : JsonArray) : Package {
            return Package(eventKey, data, fileList)
        }
    }
}

class PackageFile {
    constructor(f : JsonObject) {
        type = f.getString("type", "")
        puid = f.getString("puid", "")
        hash = f.getString("sha256_hash", "")
        size = fileSize(f.get("size"))
        lastModified = f.getString("last_modified", "")
        uuid = f.getString("uuid", "")

        val p = URLDecoder.decode(f.getString("path", ""), "UTF-8")
        name = URLDecoder.decode(f.getString("name", ""), "UTF-8")
        val n = if (type == "Folder") "${name}${File.separator}" else name
        path = if (p.endsWith(n))
            p.substringBeforeLast(n)
        else
            p
    }

    val path : String
    val name : String
    val type : String
    var puid : String
    var hash : String
    val size : Int
    val lastModified : String
    val uuid : String

    fun fullPath() : String {
        val p = if (!path.endsWith(File.separator)) path else path.substringBeforeLast(File.separator)
        return "${p}${File.separator}${name}"
    }

    fun toJson(includeFilenames: Boolean) : JsonObject {
        val fileJson = JsonBuilder()
        with (fileJson) {
            if (includeFilenames) {
                add("path", path)
                add("name", name)
            }
            add("type", type)
            add("puid", puid)
            add("sha256_hash", hash)
            add("size", size)
            add("last_modified", lastModified)
            add("uuid", uuid)
        }
        return fileJson.build()
    }

    companion object {
        private fun fileSize(o : JsonValue?) : Int {
            if (o == null)
                return 0
            if (o.valueType == JsonValue.ValueType.STRING) {
                val s = (o as JsonString).string
                return if (s != "") s.toInt() else 0
            }
            if (o.valueType == JsonValue.ValueType.NUMBER)
                return (o as JsonNumber).intValue()
            return 0
        }
    }
}
