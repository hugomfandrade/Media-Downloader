package dev.hugomfandrade.mediadownloader.core.download

import dev.hugomfandrade.mediadownloader.core.DownloadableItem
import dev.hugomfandrade.mediadownloader.core.dev.DevDownloaderTask

class DownloaderIdentifier {

    init {
        throw AssertionError()
    }

    companion object {

        val TAG = "DownloaderIdentifier"

        @Throws(IllegalAccessException::class)
        fun findTask(dirPath: String, downloadableItem: DownloadableItem): DownloaderTask {
            return findTask(dirPath.toString(), downloadableItem, downloadableItem)
        }

        @Throws(IllegalAccessException::class)
        fun findTask(dir: String, downloadableItem: DownloadableItem, listener : DownloaderTask.Listener): DownloaderTask {

            val mediaUrl = downloadableItem.mediaUrl ?: throw IllegalAccessException("mediaUrl not found")
            val filename = downloadableItem.filename ?: throw IllegalAccessException("filename not found")

            if (mediaUrl.contains("dev.com")) {
                return DevDownloaderTask(listener);
            }
            if (mediaUrl.contains(".m3u8")) {
                return TSDownloaderTask(mediaUrl, dir, filename, listener)
            }
            else {
                return RawDownloaderTask(mediaUrl, dir, filename, listener)
            }
        }
    }
}