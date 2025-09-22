package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.parsing.ParsingUtils
import org.jsoup.nodes.DataNode
import org.jsoup.nodes.Document
import java.lang.Exception
import kotlin.collections.iterator

class TSFParsingTask : ParsingTask {

    override fun isUrlSupported(url: String): Boolean {

        return url.contains("tsf.pt")
    }

    override fun parseMediaUrl(doc: Document): String? {
        try {

            val scriptElements = doc.getElementsByTag("script")?: return null

            for (scriptElement in scriptElements.iterator()) {

                for (dataNode: DataNode in scriptElement.dataNodes()) {

                    val scriptText: String = dataNode.wholeData

                    if (!scriptText.contains("@context")) continue
                    if (!scriptText.contains("@type")) continue
                    if (!scriptText.contains("VideoObject")) continue
                    if (!scriptText.contains("contentUrl")) continue


                    try {

                        val from = "\"contentUrl\": \""
                        val to = "\""

                        val link: String = scriptText.substring(
                                ParsingUtils.Companion.indexOfEx(scriptText, from),
                                ParsingUtils.Companion.indexOfEx(scriptText, from) + scriptText.substring(
                                    ParsingUtils.Companion.indexOfEx(scriptText, from)).indexOf(to))

                        return link
                    } catch (parsingException: Exception) {
                        parsingException.printStackTrace()
                    }
                }
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun parseThumbnailPath(doc: Document): String? {
        return ParsingUtils.Companion.getThumbnailFromTwitterMetadata(doc)
    }
}