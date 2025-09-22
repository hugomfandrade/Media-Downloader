package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import dev.hugomfandrade.mediadownloader.core.download.TSUtils
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingUtils
import dev.hugomfandrade.mediadownloader.core.parsing.TSPlaylist
import org.apache.commons.codec.binary.Base64
import org.jsoup.nodes.DataNode
import org.jsoup.nodes.Document
import java.lang.Exception
import kotlin.collections.iterator

open class RTPPlayParsingTaskV8 : RTPPlayTSParsingTask() {

    // get playlist url
    override fun parseMediaUrl(doc: Document): String? {

        val scriptElements = doc.getElementsByTag("script")

        val availables = scriptElements.stream()
                .map { scriptElement -> scriptElement.dataNodes() }
                .flatMap { dataNode -> dataNode.stream() }
                .map { dataNode -> dataNode.wholeData }
                .filter { scriptText -> scriptText.contains("RTPPlayer") }

        for (scriptElement in scriptElements.iterator()) {

            for (dataNode: DataNode in scriptElement.dataNodes()) {

                if (!dataNode.wholeData.contains("RTPPlayer")) continue

                var scriptText: String = dataNode.wholeData.replace("\\s+".toRegex(), "")

                try {

                    scriptText = scriptText.substring(scriptText.lastIndexOf("varf={hls"))

                    val rtpPlayerSubString: String = scriptText
                    val from = "hls:atob(decodeURIComponent("
                    val to = ".join(\"\"))"

                    if (rtpPlayerSubString.indexOf(from) < 0) continue

                    val indexFrom = ParsingUtils.Companion.indexOfEx(rtpPlayerSubString, from)

                    val fileKeyAsString = rtpPlayerSubString.substring(indexFrom, indexFrom + rtpPlayerSubString.substring(indexFrom).indexOf(to))

                    val jsonArray : JsonArray = JsonParser.parseString(fileKeyAsString).asJsonArray ?: continue

                    val link = StringBuilder()

                    for (i in 0 until jsonArray.size()) {
                        val item = jsonArray.get(i).asString

                        link.append(item)
                    }

                    val fullLink = link.toString()

                    return String(Base64.decodeBase64(ParsingUtils.Companion.decode(fullLink).toByteArray()))

                } catch (parsingException: Exception) {
                    parsingException.printStackTrace()
                }
            }
        }

        return null
    }

    override fun parseM3U8Playlist(m3u8: String): TSPlaylist? {

        val playlist : TSPlaylist? = TSUtils.Companion.getCompleteM3U8PlaylistWithoutBaseUrl(m3u8)

        playlist?.getTSUrls()?.forEach{ tsUrl ->
            tsUrl.url = m3u8.substringBeforeLast("/") + "/" + tsUrl.url
        }

        return playlist
    }
}