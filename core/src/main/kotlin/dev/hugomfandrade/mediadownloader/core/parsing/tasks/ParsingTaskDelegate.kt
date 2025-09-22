package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.parsing.ParsingData
import dev.hugomfandrade.mediadownloader.core.utils.NetworkUtils
import org.jsoup.nodes.Document

open class ParsingTaskDelegate(private val parsingTasks : List<ParsingTask>) : ParsingTask {

    override fun isUrlSupported(url: String) : Boolean {

        for (task in parsingTasks) {
            if (task.isUrlSupported(url)) return true
        }

        return false
    }

    override fun parseMediaFile(doc: Document): ParsingData? {

        val url = doc.baseUri()

        if (!NetworkUtils.Companion.isValidURL(url)) return null

        var selectedTask : ParsingTask? = null
        var selectedData : ParsingData? = null

        for (task in parsingTasks) {

            // check if url is supported
            if (!task.isUrlSupported(url)) continue

            // if is able to parse, break out of loop with selected task
            selectedData = task.parseMediaFile(doc)
            if (selectedData != null) {
                selectedTask = task
                break
            }
        }

        if (selectedTask == null || selectedData == null) return null

        return selectedData
    }

    override fun isValid(doc: Document) : Boolean {

        val url = doc.baseUri()

        if (!NetworkUtils.Companion.isValidURL(url)) return false

        for (task in parsingTasks) {

            // check if url is supported
            if (!task.isUrlSupported(url)) continue

            // if is valid, return true
            if (task.isValid(doc)) return true
        }

        return false
    }

    override fun parseMediaUrl(doc: Document): String? {
        throw RuntimeException("delegate not defined")
    }

    // never called within class
    override fun parseMediaFileName(doc: Document, mediaUrl: String): String {
        throw RuntimeException("delegate not defined")
    }
}