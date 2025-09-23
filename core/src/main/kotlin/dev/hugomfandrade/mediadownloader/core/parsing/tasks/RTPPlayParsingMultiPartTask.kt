package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.parsing.ParsingData
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.iterator

class RTPPlayParsingMultiPartTask : ParsingMultiPartTask() {

    override fun isUrlSupported(url: String): Boolean {
        return url.contains("www.rtp.pt/play")
    }

    override fun parseMediaFile(doc: Document): ParsingData? {

        val url = doc.baseUri()

        tasks.clear()
        datas.clear()

        val metadataList: ArrayList<Metadata> = getUrls(url)

        metadataList.forEach(action = { metadata ->
            val task = RTPPlayParsingTaskIdentifier()

            val data : ParsingData? = task.parseMediaFile(metadata.urlString)
            if (data != null) {
                val part = metadata.suffix
                val originalFilename = data.filename ?: "unknown"

                if (part != null) {
                    val lastDot = originalFilename.lastIndexOf(".")
                    val preFilename = originalFilename.substring(0, lastDot)
                    val extFilename = originalFilename.substring(lastDot, originalFilename.length)
                    data.filename = "$preFilename.$part$extFilename"
                }
                tasks.add(task)
                datas.add(data)
            }
        })

        return datas.stream().findFirst().orElse(null)
    }

    override fun parseMediaFileName(doc: Document, mediaUrl: String): String {
        // do nothing
        return null.toString()
    }

    override fun parseMediaUrl(doc: Document): String {
        // do nothing
        return null.toString()
    }

    override fun isValid(doc: Document) : Boolean {

        if (!RTPPlayParsingTaskIdentifier().isValid(doc)) return false

        // is Multi Part
        try {

            val sectionParts = doc.getElementsByClass("section-parts")

            for (sectionPart: Element in sectionParts.iterator()) {

                for (parts: Element in sectionPart.getElementsByClass("parts")) {

                    for (li: Element in parts.getElementsByTag("li")) {

                        /*if (li.hasClass("active")) {
                            return true
                        }*/

                        for (a: Element in li.getElementsByTag("a")) {

                            val href: String = a.attr("href")

                            if (href.isEmpty()) continue
                            return true
                        }
                    }
                }
            }

        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun getUrls(url: String): ArrayList<Metadata> {
        val urls = ArrayList<String>()
        val urlsMetadata = ArrayList<Metadata>()

        // is Multi Part
        try {
            val doc: Document?

            try {
                doc = Jsoup.connect(url).timeout(10000).get()
            } catch (ignored: IOException) {
                return urlsMetadata
            }

            val sectionParts = doc.getElementsByClass("section-parts")

            for (sectionPart: Element in sectionParts.iterator()) {

                for (parts: Element in sectionPart.getElementsByClass("parts")) {

                    for (li: Element in parts.getElementsByTag("li")) {

                        if (li.hasClass("active")) {

                            for (span: Element in li.getElementsByTag("span")) {

                                val part: String = span.html()
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                    .replace("PARTE", "P")
                                    .replace("\\s+","")
                                    .replace(" ","")

                                urls.add(url)
                                urlsMetadata.add(Metadata(url, part))
                            }
                            // urls.add(urlString)
                            continue
                        }
                        for (a: Element in li.getElementsByTag("a")) {

                            val href: String = a.attr("href")
                            val part: String = a.html()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                .replace("PARTE", "P")
                                .replace("\\s+","")
                                .replace(" ","")

                            if (href.isEmpty()) continue
                            urls.add("https://www.rtp.pt$href")
                            urlsMetadata.add(Metadata("https://www.rtp.pt$href", part))
                        }
                    }
                }
            }

        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        return urlsMetadata
    }

    data class Metadata(val urlString : String, val suffix : String?)
}