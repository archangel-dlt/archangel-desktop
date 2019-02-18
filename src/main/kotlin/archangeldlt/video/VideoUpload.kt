package archangeldlt.video

import khttp.structures.files.FileLike
import java.io.File
import khttp.post as KhttpPost

fun VideoUpload(xipKey: String, fileUuid: String, filePath: String) {
    val url = "http://blockchain.surrey.ac.uk/archangel/videos/upload/${xipKey}/${fileUuid}"
    val response = KhttpPost(
        url = url,
        files = listOf(FileLike("video", File(filePath)))
    )

    if (response.statusCode != 200)
        throw Exception("${response.statusCode}: ${response.text}")
}