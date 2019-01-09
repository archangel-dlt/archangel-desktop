package archangeldlt.ethereum

import javafx.collections.FXCollections
import tornadofx.getProperty
import tornadofx.property
import java.time.LocalDateTime
import javax.json.JsonArray
import javax.json.JsonObject

class Package {
    var citation by property<String>()
    var supplier by property<String>()
    var creator by property<String>()
    var rights by property<String>()
    var held by property<String>()

    val files = FXCollections.observableArrayList<PackageFile>()

    fun citationProperty() = getProperty(Package::citation)
    fun supplierProperty() = getProperty(Package::supplier)
    fun creatorProperty() = getProperty(Package::creator)
    fun rightsProperty() = getProperty(Package::rights)
    fun heldProperty() = getProperty(Package::held)

    fun fromEvent(data : JsonObject, fileList : JsonArray) {
        citation = data.getString("citation", "")
        supplier = data.getString("supplier", "")
        creator = data.getString("creator", "")
        rights = data.getString("rights", "")
        held = data.getString("held", "")

        fileList.forEach {
            val file = PackageFile(it.asJsonObject())
            files.add(file)
        }
    }
}

class PackageFile {
    constructor(f : JsonObject) {
        type = f.getString("type")
        puid = f.getString("puid")
        hash = f.getString("sha256_hash")
        size = f.getString("size")?.toInt()
        lastModified = LocalDateTime.parse(f.getString("last_modified"))
    }

    var name by property<String>()
    var type by property<String>()
    var puid by property<String>()
    var hash by property<String>()
    var size by property<Int>()
    var lastModified by property<LocalDateTime>()

    fun nameProperty() = getProperty(PackageFile::name)
    fun typeProperty() = getProperty(PackageFile::type)
    fun puidProperty() = getProperty(PackageFile::puid)
    fun hashProperty() = getProperty(PackageFile::hash)
    fun sizeProperty() = getProperty(PackageFile::size)
    fun lastModifiedProperty() = getProperty(PackageFile::lastModified)
}
