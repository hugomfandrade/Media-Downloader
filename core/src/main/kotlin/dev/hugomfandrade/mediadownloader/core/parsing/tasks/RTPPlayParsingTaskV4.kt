package dev.hugomfandrade.mediadownloader.core.parsing.tasks

import dev.hugomfandrade.mediadownloader.core.download.TSUtils
import dev.hugomfandrade.mediadownloader.core.parsing.TSPlaylist

@Deprecated(message = "use a more recent RTPPlay parser")
open class RTPPlayParsingTaskV4 : RTPPlayParsingTaskV3() {

    override fun parseM3U8Playlist(m3u8: String): TSPlaylist? {
        return TSUtils.Companion.getCompleteM3U8PlaylistWithoutBaseUrl(m3u8)
    }
}