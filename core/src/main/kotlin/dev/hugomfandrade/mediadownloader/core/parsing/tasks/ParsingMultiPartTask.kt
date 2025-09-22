package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.parsing.ParsingData

abstract class ParsingMultiPartTask : ParsingTask {

    val tasks : ArrayList<ParsingTask> = ArrayList()
    val datas : ArrayList<ParsingData> = ArrayList()
}