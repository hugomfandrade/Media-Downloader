package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.parsing.ParsingUtils
import org.jsoup.nodes.Document

abstract class SICTSParsingTask : TSParsingTask {

    override fun isUrlSupported(url: String): Boolean {

        return url.contains("sicradical.sapo.pt") ||
                url.contains("sicradical.pt") ||
                url.contains("sicnoticias.sapo.pt") ||
                url.contains("sicnoticias.pt") ||
                url.contains("sic.sapo.pt") ||
                url.contains("sic.pt")
    }

    override fun parseMediaFileName(doc: Document, mediaUrl: String): String {
        return ParsingUtils.Companion.getMediaFileName(doc, doc.baseUri() ?: "", mediaUrl)
                .replace("SIC.Noticias.", "")
                .replace("SIC.Radical.", "")
                .replace("SIC.", "") + ".ts"
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