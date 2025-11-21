package dev.hugomfandrade.mediadownloader.android.app.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import dev.hugomfandrade.mediadownloader.android.R
import dev.hugomfandrade.mediadownloader.android.utils.AndroidMediaUtils
import dev.hugomfandrade.mediadownloader.android.utils.LegacyAppCompatTheme
import dev.hugomfandrade.mediadownloader.ui.shared.PreferenceData
import dev.hugomfandrade.mediadownloader.ui.shared.SettingsScreen
import dev.hugomfandrade.mediadownloader.ui.shared.Toolbar
import dev.hugomfandrade.mediadownloader.ui.shared.ToolbarBackButton

class SettingsActivity : ComponentActivity() {

    companion object {

        fun makeIntent(context: Context) : Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val directoryUri = AndroidMediaUtils.getDownloadsDirectory(this)
        val directory = directoryUri.cleanPrefix()
        val title = getString(R.string.directory_storage_title)
        val summary = mutableStateOf(directory)

        val openDocumentTreeLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                if (uri != null) {
                    // Grant persistent permission
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    val docUri: Uri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
                    val path: String? = SettingsUtils.getPath(this, docUri)

                    AndroidMediaUtils.putDownloadsDirectory(this, path?:"")

                    summary.value = AndroidMediaUtils.getDownloadsDirectory(this).cleanPrefix()
                }
            }

        setContent {

            val items = listOf(
                PreferenceData.ClickablePreference(title, summary.value) {
                    openDocumentTreeLauncher.launch(AndroidMediaUtils.getDownloadsDirectory(this))
                }
            )

            LegacyAppCompatTheme {

                Column(modifier = Modifier.fillMaxSize()) {
                    Toolbar(
                        title = getString(R.string.settings).uppercase(),
                        navigationIcon = {
                            ToolbarBackButton(onClick = { onBackPressedDispatcher.onBackPressed() })
                        }
                    )
                    SettingsScreen(items)
                }
            }
        }
    }
}

private fun Uri.cleanPrefix(): String {
    return this.toString().replace("/storage/emulated/0", "")
}