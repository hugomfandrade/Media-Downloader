package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.download.TSUtils
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingUtils
import dev.hugomfandrade.mediadownloader.core.parsing.TSPlaylist
import org.jsoup.nodes.DataNode
import org.jsoup.nodes.Document
import java.lang.Exception
import kotlin.collections.iterator

@Deprecated(message = "use a more recent RTPPlay parser")
open class RTPPlayParsingTaskV3 : RTPPlayTSParsingTask() {

    // get playlist url
    override fun parseMediaUrl(doc: Document): String? {

        try {

            val scriptElements = doc.getElementsByTag("script") ?: return null

            for (scriptElement in scriptElements.iterator()) {

                for (dataNode: DataNode in scriptElement.dataNodes()) {

                    if (!dataNode.wholeData.contains("RTPPlayer")) continue

                    val scriptText: String = dataNode.wholeData.replace("\\s+".toRegex(), "")

                    try {

                        val rtpPlayerSubString: String = scriptText
                        val from = "file:\""
                        val to = "\","

                        if (rtpPlayerSubString.indexOf(from) >= 0) {
                            val indexFrom = ParsingUtils.Companion.indexOfEx(rtpPlayerSubString, from)

                            return rtpPlayerSubString.substring(indexFrom, indexFrom + rtpPlayerSubString.substring(indexFrom).indexOf(to))

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

    override fun parseM3U8Playlist(m3u8: String): TSPlaylist? {

        if (!TSUtils.Companion.getUrlWithoutParameters(m3u8).endsWith(".m3u8")) return null

        return TSPlaylist().add("DEFAULT", m3u8)
    }
}