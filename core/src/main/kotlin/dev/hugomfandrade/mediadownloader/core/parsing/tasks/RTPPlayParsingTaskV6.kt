package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingUtils
import org.jsoup.nodes.DataNode
import org.jsoup.nodes.Document
import java.lang.Exception
import kotlin.collections.iterator

@Deprecated(message = "use a more recent RTPPlay parser")
open class RTPPlayParsingTaskV6 : RTPPlayParsingTaskV5() {

    // get playlist url
    override fun parseMediaUrl(doc: Document): String? {

        val scriptElements = doc.getElementsByTag("script")

        for (scriptElement in scriptElements.iterator()) {

            for (dataNode: DataNode in scriptElement.dataNodes()) {

                if (!dataNode.wholeData.contains("RTPPlayer")) continue

                val scriptText: String = dataNode.wholeData.replace("\\s+".toRegex(), "")

                try {

                    val rtpPlayerSubString: String = scriptText
                    val from = "hls:decodeURIComponent("
                    val to = ".join(\"\"))"

                    if (rtpPlayerSubString.indexOf(from) >= 0) {
                        val indexFrom = ParsingUtils.Companion.indexOfEx(rtpPlayerSubString, from)

                        val fileKeyAsString = rtpPlayerSubString.substring(indexFrom, indexFrom + rtpPlayerSubString.substring(indexFrom).indexOf(to))

                        val jsonArray : JsonArray = JsonParser.parseString(fileKeyAsString).asJsonArray ?: continue

                        val link = StringBuilder()

                        for (i in 0 until jsonArray.size()) {
                            val item = jsonArray.get(i).asString

                            link.append(item)
                        }

                        return ParsingUtils.Companion.decode(link.toString())
                    }
                } catch (parsingException: Exception) {
                    parsingException.printStackTrace()
                }
            }
        }

        return null
    }
}