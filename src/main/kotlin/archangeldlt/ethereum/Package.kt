package archangeldlt.ethereum

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.json.JsonArray
import javax.json.JsonObject

class Package {
    val citationProperty = SimpleStringProperty()
    val supplierProperty = SimpleStringProperty()
    val creatorProperty = SimpleStringProperty()
    val rightsProperty = SimpleStringProperty()
    val heldProperty = SimpleStringProperty()

    var key : String = ""
    val citation by citationProperty
    val supplier by supplierProperty
    val creator by creatorProperty
    val rights by rightsProperty
    val held by heldProperty

    val files = FXCollections.observableArrayList<PackageFile>()

    init {
        key = UUID.randomUUID().toString()
    }

    fun fromEvent(eventKey: String, data : JsonObject, fileList : JsonArray) {
        key = eventKey
        citationProperty.value = data.getString("citation", "")
        supplierProperty.value = data.getString("supplier", "")
        creatorProperty.value = data.getString("creator", "")
        rightsProperty.value = data.getString("rights", "")
        heldProperty.value = data.getString("held", "")

        fileList.forEach {
            val file = PackageFile(it.asJsonObject())
            files.add(file)
        }
    }

    fun toJSON() : JsonObject {
        val data = JsonBuilder()
        with (data) {
            add("key", key)
            add("pack", "sip")
            add("supplier", supplier)
            add("creator", creator)
            add("rights", rights)
            add("held", held)
        }

        val fileJson = files.map { it.toJson() }

        val json = JsonBuilder()
        with (json) {
            add("data", data)
            add("files", fileJson)
            add("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        }
        return json.build()
    }
}

class PackageFile {
    constructor(f : JsonObject) {
        path = ""
        name = ""
        type = f.getString("type", "")
        puid = f.getString("puid", "")
        hash = f.getString("sha256_hash", "")
        size = f.getString("size")?.toInt()
        lastModified = LocalDateTime.parse(f.getString("last_modified"))
    }

    val path : String
    val name : String
    val type : String
    val puid : String
    val hash : String
    val size : Int?
    val lastModified : LocalDateTime

    fun toJson() : JsonObject {
        val fileJson = JsonBuilder()
        with (fileJson) {
            add("type", type)
            add("puid", puid)
            add("sha256_hash", hash)
            add("size", size)
            add("last_modified", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(lastModified))
        }
        return fileJson.build()
    }
}
