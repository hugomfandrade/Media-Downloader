package dev.hugomfandrade.mediadownloader.android.app.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.hugomfandrade.mediadownloader.android.R
import dev.hugomfandrade.mediadownloader.android.utils.AndroidMediaUtils
import dev.hugomfandrade.mediadownloader.android.utils.LegacyAppCompatTheme
import dev.hugomfandrade.mediadownloader.ui.shared.Toolbar
import dev.hugomfandrade.mediadownloader.ui.shared.ToolbarBackButton

class SettingsActivity : AppCompatActivity() {

    companion object {

        private const val REQUEST_EXTERNAL_ACCESS = 100

        fun makeIntent(context: Context) : Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val toolbarComposeView: ComposeView = findViewById(R.id.appbar_compose)
        toolbarComposeView.setContent {
            LegacyAppCompatTheme {
                Toolbar(
                    title = getString(R.string.settings).uppercase(),
                    navigationIcon = {
                        ToolbarBackButton(onClick = { onBackPressedDispatcher.onBackPressed() })
                    }
                )
            }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, SettingsFragment())
                .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private var filePicker: Preference? = null

        override fun onCreatePreferences(bundle : Bundle?, rootKey : String?) {

            setPreferencesFromResource(R.xml.preferences_main, rootKey)

            val context = activity as Context

            val filePicker : Preference = findPreference(getString(R.string.key_directory_name)) ?: return
            filePicker.setDefaultValue(AndroidMediaUtils.getDownloadsDirectory(context))
            filePicker.summary = AndroidMediaUtils.getDownloadsDirectory(context).toString().replace("/storage/emulated/0", "")
            filePicker.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                i.addCategory(Intent.CATEGORY_DEFAULT)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    i.putExtra(DocumentsContract.EXTRA_INITIAL_URI, AndroidMediaUtils.getDownloadsDirectory(context))
                }

                i.putExtra("android.content.extra.SHOW_ADVANCED", true)
                startActivityForResult(Intent.createChooser(i, getString(R.string.directory_storage_summary)), REQUEST_EXTERNAL_ACCESS)
                true
            }

            this.filePicker = filePicker
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

            if (requestCode == REQUEST_EXTERNAL_ACCESS && resultCode == RESULT_OK) {
                val uri = data?.data
                val activity = activity as Activity

                val docUri: Uri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
                val path: String? = SettingsUtils.getPath(activity, docUri)

                if (path != null) {
                    AndroidMediaUtils.putDownloadsDirectory(activity, path)

                    filePicker?.summary = AndroidMediaUtils.Companion.getDownloadsDirectory(activity).toString().replace("/storage/emulated/0", "")
                }
            }
        }
    }
}