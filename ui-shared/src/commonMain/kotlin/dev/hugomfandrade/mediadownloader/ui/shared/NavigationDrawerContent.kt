package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import media_downloader.ui_shared.generated.resources.Res
import media_downloader.ui_shared.generated.resources.ic_archive
import media_downloader.ui_shared.generated.resources.ic_launcher_foreground
import media_downloader.ui_shared.generated.resources.ic_rtpplay
import media_downloader.ui_shared.generated.resources.ic_settings
import media_downloader.ui_shared.generated.resources.ic_sic
import media_downloader.ui_shared.generated.resources.ic_sicnoticias
import media_downloader.ui_shared.generated.resources.ic_sicradical
import media_downloader.ui_shared.generated.resources.ic_tvi_player
import org.jetbrains.compose.resources.painterResource

@Composable
fun NavigationDrawer(drawerItems: Array<DrawerItem>, onClick: (DrawerItem) -> Unit) {

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        HeaderLayout()

        // Divider line
        Box(modifier = Modifier.fillMaxWidth().height(1.dp)
            .background(MaterialTheme.colorScheme.primaryContainer))

        NavigationDrawerContent(drawerItems, onClick)
    }
}

@Composable
fun HeaderLayout() {
    // Top section
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_launcher_foreground),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape), // makes it circular
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun NavigationDrawerContent(drawerItems: Array<DrawerItem>, onClick: (DrawerItem) -> Unit) {

    Column(modifier = Modifier.padding(top = 8.dp).fillMaxSize()) {
        drawerItems.forEach { item ->
            when (item) {
                is Header -> HeaderView(item)
                is OptionItem -> OptionItemView(item, onClick)
                is QuickAccessItem -> QuickAccessItemView(item, onClick)
            }
        }
    }
}

@Composable
fun OptionItemView(optionItem: OptionItem, onClick: (DrawerItem) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // Drawer item row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(onClick = { onClick(optionItem) })
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = optionItem.resource,
                contentDescription = optionItem.title,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size(36.dp)
                    .padding(horizontal = 8.dp)
            )

            Text(
                text = optionItem.title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
fun QuickAccessItemView(quickAccessItem: QuickAccessItem, onClick: (DrawerItem) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // Drawer item row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(onClick = { onClick(quickAccessItem) })
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = quickAccessItem.resource,
                contentDescription = quickAccessItem.title,
                modifier = Modifier
                    .size(36.dp)
                    .padding(horizontal = 8.dp)
            )

            Text(
                text = quickAccessItem.title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
fun HeaderView(header: Header) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = header.headerTitle.uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(end = 8.dp)
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).height(1.dp)
            )
        }
    }
}

@Composable fun iconArchive() = painterResource(Res.drawable.ic_archive)
@Composable fun iconRTPPlay() = painterResource(Res.drawable.ic_rtpplay)
@Composable fun iconTVI() = painterResource(Res.drawable.ic_tvi_player)
@Composable fun iconSICRadical() = painterResource(Res.drawable.ic_sicradical)
@Composable fun iconSICNoticias() = painterResource(Res.drawable.ic_sicnoticias)
@Composable fun iconSIC() = painterResource(Res.drawable.ic_sic)
@Composable fun iconSettings() = painterResource(Res.drawable.ic_settings)

abstract class DrawerItem
data class Header(val headerTitle: String) : DrawerItem()
data class OptionItem(val title: String, val resource: Painter, val intent: Runnable) : DrawerItem()
data class QuickAccessItem(val title: String, val resource: Painter, val url: String) : DrawerItem()
