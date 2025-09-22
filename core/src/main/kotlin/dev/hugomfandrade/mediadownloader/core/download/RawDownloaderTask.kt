package dev.hugomfandrade.mediadownloader.core.download

import dev.hugomfandrade.mediadownloader.core.utils.MediaUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URI
import java.net.URL

class RawDownloaderTask(private val mediaUrl : String,
                        private val dirPath : String,
                        private val filename : String,
                        private val listener : Listener) :

        DownloaderTask(listener) {

    override fun downloadMediaFile() {

        // check if was cancelled before actually starting
        if (tryToCancelIfNeeded()) return

        var mInputStream: InputStream? = null

        try {
            val url : URL
            try {
                url = URI.create(mediaUrl).toURL()
            }
            catch (e: Exception) {
                dispatchDownloadFailed("URL no longer exists")
                return
            }
            val inputStream = url.openStream()
            mInputStream = inputStream

            val huc = url.openConnection()
            val size = huc.contentLength.toLong()

            val storagePath = dirPath
            val f = File(storagePath, filename)
            if (MediaUtils.Companion.doesMediaFileExist(f)) {
                dispatchDownloadFailed("file with same name already exists")
                return
            }
            dispatchDownloadStarted(f)

            val fos = FileOutputStream(f)
            val buffer = ByteArray(1024)
            if (inputStream != null) {
                var len = inputStream.read(buffer)
                var progress = len.toLong()
                while (len > 0) {

                    // cancel before downloading
                    if (tryToCancelIfNeeded()) return

                    if (doPause()) return

                    fos.write(buffer, 0, len)
                    len = inputStream.read(buffer)
                    progress += len

                    // cancel after downloading
                    if (tryToCancelIfNeeded()) return

                    dispatchProgress(progress, size)
                }
            }

            dispatchDownloadFinished(f)

            fos.close()

        } catch (ioe: IOException) {
            ioe.printStackTrace()
            dispatchDownloadFailed("Internal error while downloading")
        } finally {
            try {
                mInputStream?.close()
            } catch (ioe: IOException) {
                // just going to ignore this one
            }
        }
    }
}