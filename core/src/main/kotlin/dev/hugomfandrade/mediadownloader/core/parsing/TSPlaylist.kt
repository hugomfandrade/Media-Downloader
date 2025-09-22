package dev.hugomfandrade.mediadownloader.core.parsing

/**
 * TS Playlist for the many resolutions
 */
class TSPlaylist {

    private val tsFiles = HashMap<String, TSUrl>()

    fun add(resolution : String, url : String) : TSPlaylist {
        return add(resolution, TSUrl(url))
    }

    fun add(resolution : String, url : TSUrl) : TSPlaylist {
        tsFiles[resolution] = url
        return this
    }

    fun getTSUrls() : MutableCollection<TSUrl> {
        return tsFiles.values
    }
}