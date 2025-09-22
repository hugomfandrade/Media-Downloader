package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.parsing.ParsingUtils
import org.jsoup.nodes.DataNode
import org.jsoup.nodes.Document
import java.lang.Exception
import kotlin.collections.iterator

open class SICParsingTaskV4 : SICParsingTask() {

    override fun parseMediaUrl(doc: Document): String? {
        try {

            val scriptElements = doc.getElementsByTag("script") ?: return null

            for (scriptElement in scriptElements.iterator()) {

                for (dataNode: DataNode in scriptElement.dataNodes()) {

                    val scriptText: String = dataNode.wholeData.replace("\\s+".toRegex(), "")

                    if (!scriptText.contains("jwplayer")) continue
                    if (!scriptText.contains("'playlist'")) continue
                    if (!scriptText.contains("'file'")) continue
                    if (scriptText.contains(".m3u8")) continue

                    try {

                        val jwPlayerSubString: String = scriptText.substring(
                            ParsingUtils.Companion.indexOfEx(
                                scriptText,
                                "[{"
                            ), scriptText.lastIndexOf("}]"))

                        val from = "'file':\""
                        val to = "\","

                        if (jwPlayerSubString.indexOf(from) >= 0) {

                            val startIndex =
                                ParsingUtils.Companion.indexOfEx(jwPlayerSubString, from)

                            val link: String = jwPlayerSubString.substring(
                                    startIndex,
                                    startIndex + jwPlayerSubString.substring(startIndex).indexOf(to))

                            return link
                        }
                    } catch (parsingException: Exception) { }
                }
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun parseMediaFileName(doc: Document, mediaUrl: String): String {
        val filename = ParsingUtils.Companion.getMediaFileName(doc, doc.baseUri() ?: "", mediaUrl)
                .replace("SIC.Noticias.", "")
                .replace("SIC.Radical.", "")
                .replace("SIC.", "")

        return if (filename.endsWith(".mp4")) filename else "$filename.mp4"
    }

    override fun parseThumbnailPath(doc: Document): String? {

        val filename = super.parseThumbnailPath(doc)

        return if (filename.isNullOrEmpty()) {
            ParsingUtils.Companion.getThumbnailFromTwitterMetadata(doc) ?: filename
        } else {
            filename
        }
    }
}