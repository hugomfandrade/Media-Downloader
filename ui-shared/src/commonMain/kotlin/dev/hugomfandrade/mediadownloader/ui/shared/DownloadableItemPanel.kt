package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hugomfandrade.mediadownloader.core.DownloadableItem
import media_downloader.ui_shared.generated.resources.Res
import media_downloader.ui_shared.generated.resources.ic_clear
import media_downloader.ui_shared.generated.resources.ic_launcher_foreground
import media_downloader.ui_shared.generated.resources.ic_movie
import media_downloader.ui_shared.generated.resources.ic_pause
import media_downloader.ui_shared.generated.resources.ic_play
import media_downloader.ui_shared.generated.resources.ic_refresh
import media_downloader.ui_shared.generated.resources.media_file_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun DownloadableItem(item : DownloadableItem) {

    Row(modifier = Modifier.padding(6.dp)
        .fillMaxWidth()
        .wrapContentHeight()) {

        Image(
            painter = painterResource(Res.drawable.media_file_icon),
            contentDescription = "thumbnail",
            modifier = Modifier
                .requiredHeight(60.dp)
                .requiredWidth(90.dp)
                .background(Color(0xffaaaaaa)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = item.filename?: "",
                fontSize = 14.sp,
                modifier = Modifier.background(Color.Transparent)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .requiredHeight(30.dp)
                        .weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        progress = { item.progress },
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.primary,
                        strokeCap = StrokeCap.Butt,
                        gapSize = 0.dp
                    )

                    Text(
                        text = "" + item.progress + "%",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(Color.Transparent)
                    )

                }

                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_play),
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = "resume"
                    )
                }

                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_pause),
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = "pause"
                    )
                }

                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clear),
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = "clear"
                    )
                }

                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_refresh),
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = "refresh"
                    )
                }
            }
        }
    }
}