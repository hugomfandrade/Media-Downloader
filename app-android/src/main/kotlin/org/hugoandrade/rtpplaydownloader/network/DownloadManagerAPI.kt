package org.hugoandrade.rtpplaydownloader.network

import androidx.lifecycle.LiveData
import dev.hugomfandrade.mediadownloader.core.DownloadableItem
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingData
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingTaskResult
import dev.hugomfandrade.mediadownloader.core.parsing.pagination.PaginationParserTask
import org.hugoandrade.rtpplaydownloader.utils.ListenableFuture

interface DownloadManagerAPI {

    fun getItems(): LiveData<ArrayList<DownloadableItemAction>>
    fun parseUrl(url: String): ListenableFuture<ParsingTaskResult>
    fun parsePagination(url: String, paginationTask: PaginationParserTask): ListenableFuture<ArrayList<ParsingData>>
    fun parseMore(url: String, paginationTask: PaginationParserTask): ListenableFuture<ArrayList<ParsingData>>
    fun download(parsingData: ParsingData): ListenableFuture<DownloadableItemAction>

    fun retrieveItemsFromDB()
    fun archive(downloadableItem: AndroidDownloadableItem)
    fun emptyDB()
}