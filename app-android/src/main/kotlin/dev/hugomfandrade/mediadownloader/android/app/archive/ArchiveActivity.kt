package dev.hugomfandrade.mediadownloader.android.app.archive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dev.hugomfandrade.mediadownloader.android.app.ActivityBase
import dev.hugomfandrade.mediadownloader.android.app.main.DownloadableItemDetailsDialog
import dev.hugomfandrade.mediadownloader.android.network.AndroidDownloadableItem
import dev.hugomfandrade.mediadownloader.android.network.persistence.DownloadableItemRepository
import dev.hugomfandrade.mediadownloader.android.utils.AndroidMediaUtils
import dev.hugomfandrade.mediadownloader.android.utils.ListenableFuture
import dev.hugomfandrade.mediadownloader.android.utils.ViewUtils
import dev.hugomfandrade.mediadownloader.android.R
import dev.hugomfandrade.mediadownloader.android.databinding.ActivityArchiveBinding
import java.util.concurrent.ConcurrentHashMap

class ArchiveActivity : ActivityBase() {

    companion object {

        fun makeIntent(context: Context) : Intent {
            return Intent(context, ArchiveActivity::class.java)
        }
    }

    private lateinit var binding: ActivityArchiveBinding

    private lateinit var mDatabaseModel: DownloadableItemRepository
    private lateinit var mArchivedItemsRecyclerView: RecyclerView
    private lateinit var mArchivedItemsAdapter: ArchiveItemsAdapter

    private val downloadableItems: ConcurrentHashMap<Int, AndroidDownloadableItem> = ConcurrentHashMap()

    private var detailsDialog : DownloadableItemDetailsDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDatabaseModel = DownloadableItemRepository(application)

        initializeUI()

        val future = mDatabaseModel.retrieveArchivedDownloadableItems()
        future.addCallback(object : ListenableFuture.Callback<List<AndroidDownloadableItem>> {
            override fun onFailed(errorMessage: String) {
                Log.e(TAG, errorMessage)
                Toast.makeText(this@ArchiveActivity, "Failed to get Archived Items", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: List<AndroidDownloadableItem>) {

                synchronized(this@ArchiveActivity.downloadableItems) {

                    for (item in result) {
                        downloadableItems.putIfAbsent(item.id, item)
                    }
                }

                val listItems = downloadableItems.values.toList()
                listItems.sortedWith(compareBy { it.id })

                displayDownloadableItems(listItems)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initializeUI() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_archive)

        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.archive)
            actionBar.setDisplayHomeAsUpEnabled(true)
            // actionBar.setHomeButtonEnabled(false)
        }

        val simpleItemAnimator : SimpleItemAnimator = DefaultItemAnimator()
        simpleItemAnimator.supportsChangeAnimations = false

        mArchivedItemsRecyclerView = binding.archiveItemsRecyclerView
        mArchivedItemsRecyclerView.itemAnimator = simpleItemAnimator
        mArchivedItemsRecyclerView.layoutManager =
                if (!ViewUtils.Companion.isTablet(this) && ViewUtils.Companion.isPortrait(this)) LinearLayoutManager(this)
                else GridLayoutManager(this, if (ViewUtils.Companion.isTablet(this) && !ViewUtils.Companion.isPortrait(this)) 3 else 2)
        mArchivedItemsAdapter = ArchiveItemsAdapter()
        mArchivedItemsAdapter.setListener(object : ArchiveItemsAdapter.Listener {

            override fun onItemClicked(item: AndroidDownloadableItem) {
                val dialog = detailsDialog

                if (dialog != null) {
                    dialog.show(item)
                } else {

                    detailsDialog = DownloadableItemDetailsDialog.Builder.instance(this@ArchiveActivity)
                            .setOnItemDetailsDialogListener(object : DownloadableItemDetailsDialog.OnItemDetailsListener {
                                override fun onCancelled() {
                                    detailsDialog = null
                                }

                                override fun onArchive(item: AndroidDownloadableItem) {

                                    detailsDialog?.dismiss()

                                    item.isArchived = false
                                    mDatabaseModel.updateDownloadableEntry(item)

                                    mArchivedItemsAdapter.remove(item)
                                    binding.emptyListViewGroup.visibility = if (mArchivedItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
                                }

                                override fun onRedirect(item: AndroidDownloadableItem) {

                                    detailsDialog?.dismiss()

                                    AndroidMediaUtils.Companion.openUrl(this@ArchiveActivity, item)
                                }

                                override fun onShowInFolder(item: AndroidDownloadableItem) {

                                    detailsDialog?.dismiss()

                                    AndroidMediaUtils.Companion.showInFolderIntent(this@ArchiveActivity, item)
                                }

                                override fun onPlay(item: AndroidDownloadableItem) {

                                    detailsDialog?.dismiss()

                                    AndroidMediaUtils.Companion.play(this@ArchiveActivity, item)
                                }

                            })
                            .create(item)
                    detailsDialog?.show()
                }
            }
        })
        mArchivedItemsRecyclerView.adapter = mArchivedItemsAdapter

        binding.emptyListViewGroup.visibility = if (mArchivedItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
    }

    private fun displayDownloadableItems(items: List<AndroidDownloadableItem>) {

        runOnUiThread {
            mArchivedItemsAdapter.setItems(items)
            mArchivedItemsAdapter.notifyDataSetChanged()
            mArchivedItemsRecyclerView.scrollToPosition(0)
            binding.emptyListViewGroup.visibility = if (mArchivedItemsAdapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
        }
    }
}