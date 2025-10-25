package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.ui.graphics.painter.Painter

interface NavigationDrawerInterface {
    fun addOptionItem(item: OptionItem)
    fun addItem(item: QuickAccessItem)
    fun addHeader(header: String)
    fun setOnItemClickListener(listener: OnDrawerClickListener)
}

interface OnDrawerClickListener {
    fun onItemClicked(drawerItem: DrawerItem?)
}

abstract class DrawerItem

class Header(val headerTitle: String) : DrawerItem()

data class OptionItem(@Deprecated("will be replace with Painter") val resourceID: Int, val title: String, val intent: Runnable, val resource: Painter?) : DrawerItem()

data class QuickAccessItem(@Deprecated("will be replace with Painter") val resourceID: Int, val title: String, val url: String, val resource: Painter?) : DrawerItem()
