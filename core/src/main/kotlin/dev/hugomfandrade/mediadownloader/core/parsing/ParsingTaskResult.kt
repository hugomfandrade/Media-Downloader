package dev.hugomfandrade.mediadownloader.core.parsing

import dev.hugomfandrade.mediadownloader.core.parsing.pagination.PaginationParserTask

data class ParsingTaskResult(val parsingDatas : ArrayList<ParsingData>,
                             val paginationTask : PaginationParserTask?) {

    constructor(parsingData: ParsingData, paginationTask : PaginationParserTask?) :
            this(arrayListOf(parsingData), paginationTask)
}