package dev.hugomfandrade.mediadownloader.android

interface Config {

    companion object {

        const val nParsingThreads: Int = 10
        const val nDownloadThreads: Int = 3
        const val nPersistenceThreads: Int = 5
        const val nImageLoadingThreads: Int = 10

        const val enablePauseResume = true
    }
}