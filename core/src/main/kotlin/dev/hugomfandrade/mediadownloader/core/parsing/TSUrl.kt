package dev.hugomfandrade.mediadownloader.core.parsing

data class TSUrl(var url : String,
                 val bandwidth : Int? = null,
                 val resolution : IntArray? = null) {

}