package org.hugoandrade.rtpplaydownloader.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import dev.hugomfandrade.mediadownloader.core.DownloadableItem
import dev.hugomfandrade.mediadownloader.core.utils.MediaUtils
import org.hugoandrade.rtpplaydownloader.R
import java.io.File

class AndroidMediaUtils

/**
 * Ensure this class is only used as a utility.
 */
private constructor() {

    init {
        throw AssertionError()
    }

    companion object {

        const val sharedPreferencesName = "org.hugoandrade.rtpplaydownloader"
        const val directoryKey = "org.hugoandrade.rtpplaydownloader.directoryKey"

        // private val defaultDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        private fun getDefaultDir() : File {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        }

        fun getDownloadsDirectory(context: Context) : Uri {

            try {
                return Uri.parse(context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
                        .getString(directoryKey, getDefaultDir().toString()))
            }
            catch (e : Exception) {
                return Uri.fromFile(getDefaultDir())
            }
        }

        fun putDownloadsDirectory(context: Context, uri: Uri) {
            putDownloadsDirectory(context, uri.toString())
        }

        fun putDownloadsDirectory(context: Context, uri: String) {

            context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
                    .edit()
                    .putString(directoryKey, uri)
                    .apply()
        }

        fun showInFolderIntent(context: Context, item: DownloadableItem) {

            try {
                val dir = Uri.parse(File(item.filepath).parentFile.absolutePath + File.separator)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, dir)
                    }

                    intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_folder)))
                } else {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setDataAndType(dir, "*/*")
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_folder)))
                }

            } catch (e: Exception) { }
        }

        fun openUrl(context: Context, item: DownloadableItem) {

            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
            } catch (e: Exception) { }
        }

        fun play(context: Context, item: DownloadableItem) {

            try {
                val filepath = item.filepath
                if (MediaUtils.doesMediaFileExist(item)) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(filepath))
                            .setDataAndType(Uri.parse(filepath), "video/mp4"))
                } else {
                    ViewUtils.showToast(context, context.getString(R.string.file_not_found))
                }
            } catch (ignored: Exception) {
            }
        }
    }
}