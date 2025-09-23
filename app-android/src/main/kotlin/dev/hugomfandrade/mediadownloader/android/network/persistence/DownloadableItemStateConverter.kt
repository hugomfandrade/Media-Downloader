package dev.hugomfandrade.mediadownloader.android.network.persistence

import androidx.room.TypeConverter
import dev.hugomfandrade.mediadownloader.core.DownloadableItem

class DownloadableItemStateConverter {

    @TypeConverter
    fun toOrdinal(state: DownloadableItem.State?): Int {
        return state?.ordinal ?: -1
    }

    @TypeConverter
    fun fromOrdinal(stateOrdinal : Int?): DownloadableItem.State? {
        return if (stateOrdinal == null || stateOrdinal < 0 || stateOrdinal >= DownloadableItem.State.entries.size) null
        else enumValues<DownloadableItem.State>()[stateOrdinal]
    }
}