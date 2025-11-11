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
        OptionItem("Archive", painterResource(Res.drawable.ic_archive), {}),
        Header("Quick Access"),
        QuickAccessItem("RTP Play", painterResource(Res.drawable.ic_rtpplay), "https://www.rtp.pt/play/"),
        QuickAccessItem("TVI Player", painterResource(Res.drawable.ic_tvi_player), "https://sicradical.pt/"),
        QuickAccessItem("SIC Radical", painterResource(Res.drawable.ic_sicradical), "https://sicradical.pt/"),
        QuickAccessItem("SIC Notícias", painterResource(Res.drawable.ic_sicnoticias), "https://sicnoticias.pt/"),
        QuickAccessItem("SIC", painterResource(Res.drawable.ic_sic), "https://sic.pt/"),
        Header(""),
        OptionItem("Settings", painterResource(Res.drawable.ic_settings), {})
    )
    MaterialTheme {
        NavigationDrawer(drawerItems, {})
    }
}

@Preview
@Composable
fun NavigationDrawerContentPreview() {
    val drawerItems = arrayOf(
        OptionItem("Archive", painterResource(Res.drawable.ic_archive), {}),
        Header("Quick Access"),
        QuickAccessItem("RTP Play", painterResource(Res.drawable.ic_rtpplay), "https://www.rtp.pt/play/"),
        QuickAccessItem("TVI Player", painterResource(Res.drawable.ic_tvi_player), "https://sicradical.pt/"),
        QuickAccessItem("SIC Radical", painterResource(Res.drawable.ic_sicradical), "https://sicradical.pt/"),
        QuickAccessItem("SIC Notícias", painterResource(Res.drawable.ic_sicnoticias), "https://sicnoticias.pt/"),
        QuickAccessItem("SIC", painterResource(Res.drawable.ic_sic), "https://sic.pt/"),
        Header(""),
        OptionItem("Settings", painterResource(Res.drawable.ic_settings), {})
    )
    MaterialTheme {
        NavigationDrawerContent(drawerItems, {})
    }
}