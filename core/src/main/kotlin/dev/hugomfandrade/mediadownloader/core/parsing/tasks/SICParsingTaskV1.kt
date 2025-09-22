package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.utils.MediaUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.Exception
import kotlin.collections.elementAt
import kotlin.collections.iterator

@Deprecated(message = "use a more recent SIC parser")
open class SICParsingTaskV1 : SICParsingTask() {

    override fun parseMediaUrl(doc: Document): String? {
        try {

            val videoElements = doc.getElementsByTag("video")
            val url :String = doc.baseUri()

            for (videoElement in videoElements.iterator()) {

                for (sourceElement: Element in videoElement.getElementsByTag("source")) {

                    val src : String = sourceElement.attr("src")
                    val type: String = sourceElement.attr("type")

                    if (src.isEmpty() || type.isEmpty()) continue

                    val location : String = when {
                        url.contains("http://") -> "http:"
                        url.contains("https://") -> "https:"
                        else -> ""
                    }

                    return location + src
                }
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun parseMediaFileName(doc: Document, mediaUrl: String): String {

        try {

            val videoElements = doc.getElementsByTag("video")
            var type: String? = null

            for (videoElement in videoElements.iterator()) {

                for (sourceElement: Element in videoElement.getElementsByTag("source")) {

                    val src : String = sourceElement.attr("src")
                    type = sourceElement.attr("type")

                    if (src.isEmpty() || type.isEmpty()) continue
                    break
                }
            }

            val titleElements = doc.getElementsByTag("title")

            if (titleElements.isNotEmpty()) {

                val title: String = MediaUtils.Companion.getTitleAsFilename(titleElements.elementAt(0).text())
                        .replace("SIC.Noticias.", "")
                        .replace("SIC.Radical.", "")
                        .replace("SIC.", "")

                if (type != null && type.contains("video/mp4")) {  // is video file
                    return "$title.mp4"
                }

                return title
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        return mediaUrl
    }
}