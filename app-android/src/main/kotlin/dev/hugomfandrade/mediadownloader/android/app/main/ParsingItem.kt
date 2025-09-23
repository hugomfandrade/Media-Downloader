package dev.hugomfandrade.mediadownloader.android.app.main

import androidx.databinding.ObservableBoolean
import dev.hugomfandrade.mediadownloader.core.parsing.ParsingData

data class ParsingItem(val parsingData: ParsingData, val isSelected : ObservableBoolean)