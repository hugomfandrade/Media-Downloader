package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import media_downloader.ui_shared.generated.resources.Res
import media_downloader.ui_shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Preview
@Composable
fun NavigationDrawerPreview() {
    val drawerItems = arrayOf(
        OptionItem(0, "Archive", {}, painterResource(Res.drawable.ic_archive)),
        Header("Quick Access"),
        QuickAccessItem(0, "RTP Play", "https://www.rtp.pt/play/", painterResource(Res.drawable.ic_rtpplay)),
        QuickAccessItem(0, "TVI Player", "https://tviplayer.iol.pt/", painterResource(Res.drawable.ic_tvi_player)),
        QuickAccessItem(0, "SIC Radical", "https://sicradical.pt/", painterResource(Res.drawable.ic_sicradical)),
        QuickAccessItem(0, "SIC Notícias", "https://sicnoticias.pt/", painterResource(Res.drawable.ic_sicnoticias)),
        QuickAccessItem(0, "SIC", "https://sic.pt/", painterResource(Res.drawable.ic_sic)),
        Header(""),
        OptionItem(0, "Settings", {}, painterResource(Res.drawable.ic_settings))
    )
    MaterialTheme {
        NavigationDrawer(drawerItems, {})
    }
}

@Preview
@Composable
fun NavigationDrawerContentPreview() {
    val drawerItems = arrayOf(
        OptionItem(0, "Archive", {}, painterResource(Res.drawable.ic_archive)),
        Header("Quick Access"),
        QuickAccessItem(0, "RTP Play", "https://www.rtp.pt/play/", painterResource(Res.drawable.ic_rtpplay)),
        QuickAccessItem(0, "TVI Player", "https://tviplayer.iol.pt/", painterResource(Res.drawable.ic_tvi_player)),
        QuickAccessItem(0, "SIC Radical", "https://sicradical.pt/", painterResource(Res.drawable.ic_sicradical)),
        QuickAccessItem(0, "SIC Notícias", "https://sicnoticias.pt/", painterResource(Res.drawable.ic_sicnoticias)),
        QuickAccessItem(0, "SIC", "https://sic.pt/", painterResource(Res.drawable.ic_sic)),
        Header(""),
        OptionItem(0, "Settings", {}, painterResource(Res.drawable.ic_settings))
    )
    MaterialTheme {
        NavigationDrawerContent(drawerItems, {})
    }
}