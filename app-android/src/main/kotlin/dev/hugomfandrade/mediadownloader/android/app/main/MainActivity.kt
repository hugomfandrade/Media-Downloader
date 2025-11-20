package dev.hugomfandrade.mediadownloader.android.app.main

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.hugomfandrade.mediadownloader.android.DevConstants
import dev.hugomfandrade.mediadownloader.android.R
import dev.hugomfandrade.mediadownloader.android.app.ActivityBase
import dev.hugomfandrade.mediadownloader.android.app.archive.ArchiveActivity
import dev.hugomfandrade.mediadownloader.android.app.settings.SettingsActivity
import dev.hugomfandrade.mediadownloader.android.databinding.ActivityMainBinding
import dev.hugomfandrade.mediadownloader.android.network.AndroidDownloadableItem
import dev.hugomfandrade.mediadownloader.android.network.DownloadManager
import dev.hugomfandrade.mediadownloader.android.network.DownloadableItemAction
import dev.hugomfandrade.mediadownloader.android.utils.AndroidMediaUtils
import dev.hugomfandrade.mediadownloader.android.utils.LegacyAppCompatTheme
import dev.hugomfandrade.mediadownloader.android.utils.ListenableFuture
import dev.hugomfandrade.mediadownloader.android.utils.VersionUtils
import dev.hugomfandrade.mediadownloader.android.utils.ViewUtils
import dev.hugomfandrade.mediadownloader.core.DownloadableItem
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingData
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingTaskResult
import dev.hugomfandrade.mediadownloader.core.parsing.pagination.PaginationParserTask
import dev.hugomfandrade.mediadownloader.core.utils.FilenameLockerAdapter
import dev.hugomfandrade.mediadownloader.ui.shared.Header
import dev.hugomfandrade.mediadownloader.ui.shared.NavigationDrawer
import dev.hugomfandrade.mediadownloader.ui.shared.OptionItem
import dev.hugomfandrade.mediadownloader.ui.shared.QuickAccessItem
import dev.hugomfandrade.mediadownloader.ui.shared.SearchBarOverlay
import dev.hugomfandrade.mediadownloader.ui.shared.Toolbar
import dev.hugomfandrade.mediadownloader.ui.shared.ToolbarBackButton
import dev.hugomfandrade.mediadownloader.ui.shared.iconArchive
import dev.hugomfandrade.mediadownloader.ui.shared.iconRTPPlay
import dev.hugomfandrade.mediadownloader.ui.shared.iconSIC
import dev.hugomfandrade.mediadownloader.ui.shared.iconSICNoticias
import dev.hugomfandrade.mediadownloader.ui.shared.iconSICRadical
import dev.hugomfandrade.mediadownloader.ui.shared.iconSettings
import dev.hugomfandrade.mediadownloader.ui.shared.iconTVI

class MainActivity : ActivityBase() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mDownloadItemsRecyclerView: RecyclerView
    private lateinit var mDownloadItemsAdapter: DownloadItemsAdapter

    private lateinit var mDownloadManager: DownloadManager

    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mPendingRunnable: Runnable? = null
    private val mHandler = Handler(Looper.getMainLooper())

    private var query = mutableStateOf("")

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        extractActionSendIntentAndUpdateUI(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeUI()

        mDownloadManager = ViewModelProvider(this).get(DownloadManager::class.java)
        mDownloadManager.retrieveItemsFromDB()
        mDownloadManager.getItems().observe(this, { actions ->

            actions.forEach{ action -> action.addActionListener(actionListener)}
            mDownloadItemsAdapter.set(actions)
            mDownloadItemsAdapter.notifyDataSetChanged()
            mDownloadItemsRecyclerView.scrollToPosition(0)
            binding.emptyListViewGroup.visibility = if (mDownloadItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggle
        mDrawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawerToggle = mDrawerToggle

        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) return true

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // close if drawer is open
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        // back press
        else {
            super.onBackPressed()
        }
    }


    private fun initializeUI() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val toolbarComposeView: ComposeView = findViewById(R.id.appbar_compose)
        toolbarComposeView.setContent {

            LegacyAppCompatTheme {

                //
                val devUrl: String? = DevConstants.url
                if (devUrl != null) {
                    query.value = devUrl
                } else {
                    ViewUtils.hideSoftKeyboardAndClearFocus(binding.root)
                }

                query = remember { mutableStateOf("") }
                var searching by remember { mutableStateOf(false) }

                val drawerArrow = DrawerArrowDrawable(this).apply { color = Color.WHITE }
                var drawerProgress by remember { mutableFloatStateOf(0f) }

                binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                        drawerProgress = slideOffset
                    }

                    override fun onDrawerStateChanged(newState: Int) {
                        super.onDrawerStateChanged(newState)
                        searching = false
                    }
                })

                val toolbarText = if (searching) "" else getString(R.string.app_name).uppercase()
                val animatedNavigationIcon = @Composable {
                    AnimatedDrawerNavigationIcon(
                        drawerArrow,
                        progress = drawerProgress,
                        onClick = {
                            // Forward navigation click to DrawerToggle
                            val drawer = binding.drawerLayout
                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.closeDrawer(GravityCompat.START)
                            } else {
                                drawer.openDrawer(GravityCompat.START)
                            }
                        }
                    )
                }

                Toolbar(
                    title = toolbarText,
                    navigationIcon = {
                        if (searching)
                            ToolbarBackButton(onClick = {
                                searching = false
                                query.value = ""
                                // onQueryChange("")
                                // onSearchFocused(false)
                                // onBackPressed()
                            })
                        else
                            animatedNavigationIcon.invoke()
                    },
                    actions = {
                        if (!searching) {
                            IconButton(onClick = {
                                searching = true
                                // onSearchFocused(true)
                            }) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
                            }
                        }
                    }
                )

                if (searching) {
                    SearchBarOverlay(
                        query = query.value,
                        onClose = {
                            searching = false
                            query.value = ""
                            // onQueryChange("")
                            // onSearchFocused(false)
                        },
                        onSearch = {
                            query -> doDownload(query)
                            searching = false
                        }
                    )
                }
            }
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        val drawerToggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                val pendingRunnable = mPendingRunnable
                if (pendingRunnable != null) {
                    mHandler.post(pendingRunnable)
                    mPendingRunnable = null
                }
            }
        }
        binding.drawerLayout.addDrawerListener(drawerToggle)
        binding.drawerLayout.navigationDrawerContentCompose?.setContent {
            val drawerItems = arrayOf(
                OptionItem(getString(R.string.archive), iconArchive(), { startActivity(ArchiveActivity.makeIntent(this)) }),
                Header("Quick Access"),
                QuickAccessItem("RTP Play", iconRTPPlay(), "https://www.rtp.pt/play/"),
                QuickAccessItem("TVI Player", iconTVI(), "https://tviplayer.iol.pt/"),
                QuickAccessItem("SIC Radical", iconSICRadical(), "https://sicradical.pt/"),
                QuickAccessItem("SIC Notícias", iconSICNoticias(), "https://sicnoticias.pt/"),
                QuickAccessItem("SIC", iconSIC(), "https://sic.pt/"),
                Header(""),
                OptionItem(getString(R.string.settings), iconSettings(), { startActivity(SettingsActivity.makeIntent(this)) })
            )
            LegacyAppCompatTheme {
                NavigationDrawer(drawerItems) {
                        drawerItem ->
                    if (drawerItem is QuickAccessItem) {
                        mPendingRunnable = Runnable {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, drawerItem.url.toUri())
                                startActivity(browserIntent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    else if (drawerItem is OptionItem) {
                        mPendingRunnable = Runnable {
                            try {
                                drawerItem.intent.run()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        }

        this.mDrawerToggle = drawerToggle

        val simpleItemAnimator = DefaultItemAnimator()
        simpleItemAnimator.supportsChangeAnimations = false

        mDownloadItemsRecyclerView = binding.downloadItemsRecyclerView
        mDownloadItemsRecyclerView.itemAnimator = simpleItemAnimator
        mDownloadItemsRecyclerView.layoutManager =
                if (!ViewUtils.isTablet(this) && ViewUtils.isPortrait(this)) LinearLayoutManager(this)
                else GridLayoutManager(this, if (ViewUtils.isTablet(this) && !ViewUtils.isPortrait(this)) 3 else 2)
        mDownloadItemsAdapter = DownloadItemsAdapter()
        mDownloadItemsRecyclerView.adapter = mDownloadItemsAdapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return super.getSwipeEscapeVelocity(defaultValue) * 5
            }

            override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
                return super.getSwipeVelocityThreshold(defaultValue) * 0.2f
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder1: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, p: Int) {
                val position = viewHolder.bindingAdapterPosition
                val downloadableItem = mDownloadItemsAdapter.get(position)
                if (downloadableItem.isDownloading()) {
                    downloadableItem.cancel()
                }
                mDownloadManager.archive(downloadableItem.item)
                mDownloadItemsAdapter.remove(downloadableItem)
                binding.emptyListViewGroup.visibility = if (mDownloadItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
            }
        }).attachToRecyclerView(mDownloadItemsRecyclerView)

        binding.emptyListViewGroup.visibility = if (mDownloadItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
    }

    private fun displayDownloadableItem(action: DownloadableItemAction) {
        action.addActionListener(actionListener)

        uploadHistoryMap[action.item.id] = action

        action.item.addDownloadStateChangeListener(changeListener)

        runOnUiThread {
            mDownloadItemsAdapter.add(action)
            binding.downloadItemsRecyclerView.scrollToPosition(0)
            binding.emptyListViewGroup.visibility = if (mDownloadItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
        }
    }

    private val actionListener: DownloadableItemAction.Listener = object : DownloadableItemAction.Listener {

        override fun onPlay(action: DownloadableItemAction) {

            val dialog = detailsDialog

            if (dialog != null) {
                dialog.show(action.item)
            }
            else {

                detailsDialog = DownloadableItemDetailsDialog.Builder.instance(this@MainActivity)
                        .setOnItemDetailsDialogListener(object : DownloadableItemDetailsDialog.OnItemDetailsListener {
                            override fun onCancelled() {
                                detailsDialog = null
                            }

                            override fun onArchive(item: AndroidDownloadableItem) {

                                detailsDialog?.dismiss()

                                mDownloadManager.archive(item)
                                mDownloadItemsAdapter.remove(item)
                                binding.emptyListViewGroup.visibility = if (mDownloadItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
                            }

                            override fun onRedirect(item: AndroidDownloadableItem) {

                                detailsDialog?.dismiss()

                                AndroidMediaUtils.openUrl(this@MainActivity, item)
                            }

                            override fun onShowInFolder(item: AndroidDownloadableItem) {

                                detailsDialog?.dismiss()

                                AndroidMediaUtils.showInFolderIntent(this@MainActivity, item)
                            }

                            override fun onPlay(item: AndroidDownloadableItem) {

                                detailsDialog?.dismiss()

                                AndroidMediaUtils.play(this@MainActivity, item)
                            }

                        })
                        .create(action.item)
                detailsDialog?.show()
            }
        }

        override fun onRefresh(action: DownloadableItemAction) {
            // no-ops
        }
    }

    private fun extractActionSendIntentAndUpdateUI(intent: Intent?) {
        if (intent == null) return

        val action: String = intent.action ?: return

        if (action != Intent.ACTION_SEND || !intent.hasExtra(Intent.EXTRA_TEXT)) return

        val url: String = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return

        intent.removeExtra(Intent.EXTRA_TEXT)

        query.value = url
    }

    private var parsingDialog : ParsingDialog? = null
    private var detailsDialog : DownloadableItemDetailsDialog? = null

    @Synchronized
    private fun doDownload(url: String) {

        if (!PermissionUtils.hasGrantedPermissionAndRequestIfNeeded(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) return

        val isParsing : Boolean = parsingDialog?.isShowing() ?: false

        if (isParsing) {
            return
        }

        // dismiss previous instance
        parsingDialog?.dismiss()

        val future : ListenableFuture<ParsingTaskResult> = mDownloadManager.parseUrl(url)
        future.addCallback(object : ListenableFuture.Callback<ParsingTaskResult> {

            override fun onSuccess(result: ParsingTaskResult) {

                runOnUiThread {
                    parsingDialog?.showParsingResult(result)
                }
            }

            override fun onFailed(errorMessage: String) {

                runOnUiThread {
                    ViewUtils.showSnackBar(binding.root, getString(R.string.unable_to_parse))

                    parsingDialog?.dismiss()
                    parsingDialog = null
                }
            }
        })

        val parsingDialogListener = object : ParsingDialog.OnParsingListener {

            var paginationFuture : ListenableFuture<ArrayList<ParsingData>>? = null
            var paginationMoreFuture : ListenableFuture<ArrayList<ParsingData>>? = null

            override fun onCancelled() {
                // Toast.makeText(this@MainActivity, "ON CANCELLED", Toast.LENGTH_LONG).show()
                future.failed("parsing was cancelled")
                paginationFuture?.failed("parsing was cancelled")
                paginationMoreFuture?.failed("parsing was cancelled")
                FilenameLockerAdapter.instance.clear()
            }

            override fun onDownload(parsingDatas : ArrayList<ParsingData>) {
                parsingDatas.forEach(action = { parsingData ->
                    val filename: String? = parsingData.filename
                    if (filename != null) {
                        FilenameLockerAdapter.instance.putUnremovable(filename)
                    }
                    startDownload(parsingData)
                })

                parsingDialog?.dismiss()
                parsingDialog = null
            }

            override fun onParseEntireSeries(paginationTask: PaginationParserTask) {
                FilenameLockerAdapter.instance.clear()
                parsingDialog?.loading()
                paginationFuture = mDownloadManager.parsePagination(url, paginationTask)
                paginationFuture?.addCallback(object : ListenableFuture.Callback<ArrayList<ParsingData>> {

                    override fun onSuccess(result: ArrayList<ParsingData>) {

                        runOnUiThread {
                            parsingDialog?.showPaginationResult(paginationTask, result)
                        }
                    }

                    override fun onFailed(errorMessage: String) {

                        runOnUiThread {

                            ViewUtils.showSnackBar(binding.root, getString(R.string.unable_to_parse_pagination))

                            parsingDialog?.dismiss()
                            parsingDialog = null
                        }
                    }
                })
            }

            override fun onParseMore(paginationTask: PaginationParserTask) {
                parsingDialog?.loadingMore()
                paginationMoreFuture = mDownloadManager.parseMore(url, paginationTask)
                paginationMoreFuture?.addCallback(object : ListenableFuture.Callback<ArrayList<ParsingData>> {

                    override fun onSuccess(result: ArrayList<ParsingData>) {

                        runOnUiThread {
                            parsingDialog?.showPaginationMoreResult(paginationTask, result)
                        }
                    }

                    override fun onFailed(errorMessage: String) {

                        runOnUiThread {
                            ViewUtils.showSnackBar(binding.root, getString(R.string.unable_to_parse_pagination))

                            parsingDialog?.dismiss()
                            parsingDialog = null
                        }
                    }
                })

            }
        }

        parsingDialog = ParsingDialog.Builder.instance(this)
                .setOnParsingDialogListener(parsingDialogListener)
                .create()

        parsingDialog?.show()
    }

    private fun startDownload(parsingData: ParsingData) {
        val future = mDownloadManager.download(parsingData)
        future.addCallback(object : ListenableFuture.Callback<DownloadableItemAction> {
            override fun onFailed(errorMessage: String) {
                Log.e(TAG, errorMessage)
            }

            override fun onSuccess(result: DownloadableItemAction) {
                displayDownloadableItem(result)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (permissions.contains(permission) && PermissionUtils.hasGrantedPermission(this, permission)) {

            doDownload(query.value)
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val changeListener = object : DownloadableItem.State.ChangeListener {

        override fun onDownloadStateChange(downloadableItem: DownloadableItem) {
            // listen for end of download and show message
            if (downloadableItem.state == DownloadableItem.State.End) {
                runOnUiThread {
                    val message = getString(R.string.finished_downloading) + " " + downloadableItem.filename
                    Log.e(TAG, message)
                    ViewUtils.showSnackBar(binding.root, message)
                }
                downloadableItem.removeDownloadStateChangeListener(this)

                // upload history
                val action = uploadHistoryMap[downloadableItem.id]?: return

                VersionUtils.uploadHistory(this@MainActivity, action)
            }
        }
    }

    private val uploadHistoryMap : HashMap<Int, DownloadableItemAction> = HashMap()
}

// Android-specific
@Composable
fun AnimatedDrawerNavigationIcon(drawerArrow: DrawerArrowDrawable,
                                 progress: Float,
                                 onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        drawerArrow.progress = progress

        val painter = AndroidDrawablePainter(drawerArrow)
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = androidx.compose.ui.graphics.Color.White
        )
    }
}

class AndroidDrawablePainter(private val drawable: Drawable) : Painter() {
    override val intrinsicSize get() = Size(
        drawable.intrinsicWidth.toFloat(),
        drawable.intrinsicHeight.toFloat())

    override fun DrawScope.onDraw() {
        // set bounds to current canvas size (in px)
        val w = size.width.toInt()
        val h = size.height.toInt()
        drawable.setBounds(0, 0, w, h)

        drawIntoCanvas { canvas ->
            drawable.draw(canvas.nativeCanvas)
        }
    }
}