package dev.hugomfandrade.mediadownloader.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import dev.hugomfandrade.mediadownloader.core.DownloadableItem
import dev.hugomfandrade.mediadownloader.core.utils.MediaUtils
import dev.hugomfandrade.mediadownloader.android.R
import java.io.File
import androidx.core.net.toUri
import androidx.core.content.edit

class AndroidMediaUtils

/**
 * Ensure this class is only used as a utility.
 */
private constructor() {

    init {
        throw AssertionError()
    }

    companion object {

        const val sharedPreferencesName = "dev.hugomfandrade.mediadownloader.android"
        const val directoryKey = "dev.hugomfandrade.mediadownloader.android.directoryKey"

        // private val defaultDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        private fun getDefaultDir() : File {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        }

        fun getDownloadsDirectory(context: Context) : Uri {

            try {
                return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
                    .getString(directoryKey, getDefaultDir().toString())!!.toUri()
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
                    .edit {
                        putString(directoryKey, uri)
                    }
        }

        fun showInFolderIntent(context: Context, item: DownloadableItem) {

            try {
                val dir = (File(item.filepath!!).parentFile!!.absolutePath + File.separator).toUri()

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                intent.addCategory(Intent.CATEGORY_DEFAULT)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, dir)
                }

                intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_folder)))

            } catch (e: Exception) { }
        }

        fun openUrl(context: Context, item: DownloadableItem) {

            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, item.url.toUri()))
            } catch (e: Exception) { }
        }

        fun play(context: Context, item: DownloadableItem) {

            try {
                val filepath = item.filepath
                if (MediaUtils.doesMediaFileExist(item)) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, filepath?.toUri())
                            .setDataAndType(filepath?.toUri(), "video/mp4"))
                } else {
                    ViewUtils.showToast(context, context.getString(R.string.file_not_found))
                }
            } catch (ignored: Exception) {
            }
        }
    }
}