package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import dev.hugomfandrade.mediadownloader.core.utils.MediaUtils
import dev.hugomfandrade.mediadownloader.core.utils.MediaUtils.Companion.humanReadableByteCount
import dev.hugomfandrade.mediadownloader.core.utils.MediaUtils.Companion.humanReadableTime
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource
import media_downloader.ui_shared.generated.resources.Res
import media_downloader.ui_shared.generated.resources.did_not_download
import media_downloader.ui_shared.generated.resources.ic_clear
import media_downloader.ui_shared.generated.resources.ic_pause
import media_downloader.ui_shared.generated.resources.ic_play
import media_downloader.ui_shared.generated.resources.ic_refresh
import media_downloader.ui_shared.generated.resources.media_file_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

const val ENABLE_PAUSE_RESUME: Boolean = false

@Composable
fun DownloadableItemView(item: DownloadableItem,
                         onPlay: () -> Unit = {},
                         onResume: () -> Unit = {},
                         onPause: () -> Unit = {},
                         onClear: () -> Unit = {},
                         onRefresh: () -> Unit = {}) {

    Row(modifier = Modifier.padding(6.dp)
        .fillMaxWidth()
        .wrapContentHeight()
        .clickable{ onPlay() }) {

        // thumbnail
        RemoteImage(
            url = item.thumbnailUrl,
            contentDescription = item.filename,
            modifier = Modifier
                .requiredHeight(60.dp)
                .requiredWidth(90.dp)
                .background(Color(0xffaaaaaa)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.fillMaxWidth()) {

            // filename
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

                    // progress

                    val progress : Float
                    when (item.state) {
                        DownloadableItem.State.Start,
                        DownloadableItem.State.Failed -> {
                            progress = 0.0f
                        }
                        DownloadableItem.State.Downloading -> {
                            item.updateProgressUtils()
                            progress = item.progress
                        }
                        DownloadableItem.State.End -> {
                            progress = 1.0f
                        }
                        else -> {
                            progress = 0.0f
                        }
                    }

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        progress = { progress },
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.primary,
                        strokeCap = StrokeCap.Butt,
                        gapSize = 0.dp
                    )

                    // progress text

                    var progressText : String
                    when (item.state) {
                        DownloadableItem.State.Start -> {
                            progressText = ""
                        }
                        DownloadableItem.State.Downloading -> {
                            progressText = Math.round(item.progress * 100f).toString() + "%"
                            progressText = humanReadableByteCount(item.progressSize) + "\\" +
                                        humanReadableByteCount(item.filesize)
                            progressText =
                                humanReadableByteCount(item.downloadingSpeed.toLong()) + "ps, " +
                                        humanReadableTime(item.remainingTime)
                        }
                        DownloadableItem.State.End -> {
                            progressText = "100%"
                            progressText = humanReadableByteCount(item.filesize, true)
                        }
                        DownloadableItem.State.Failed -> {
                            progressText = item.downloadMessage?: stringResource(Res.string.did_not_download)
                        }
                        else -> {
                            progressText = ""
                        }
                    }

                    Text(
                        text = progressText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(Color.Transparent)
                    )

                }


                val isInDownloadingState : Boolean =
                    item.state == DownloadableItem.State.Downloading ||
                            item.state == DownloadableItem.State.Paused ||
                            item.state == DownloadableItem.State.Start
                val isDownloading : Boolean = item.state == DownloadableItem.State.Downloading //downloadableItemAction.isDownloading()
                val isResumed : Boolean = item.state == DownloadableItem.State.Start //downloadableItemAction.isResumed()

                if (ENABLE_PAUSE_RESUME && isInDownloadingState) {
                    if (isResumed) {
                        IconButton(
                            modifier = Modifier.size(36.dp),
                            onClick = onResume) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_play),
                                tint = MaterialTheme.colorScheme.tertiary,
                                contentDescription = "resume"
                            )
                        }
                    }

                    if (!isResumed) {
                        IconButton(
                            modifier = Modifier.size(36.dp),
                            onClick = onPause) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_pause),
                                tint = MaterialTheme.colorScheme.tertiary,
                                contentDescription = "pause"
                            )
                        }
                    }
                }

                if (isInDownloadingState) {

                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = onClear
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_clear),
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = "cancel"
                        )
                    }
                }
                if (!isInDownloadingState) {
                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = onRefresh
                    ) {
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
}

@Composable
fun RemoteImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center
) {
    val resource = asyncPainterResource(data = url?: "")

    when (resource) {
        is Resource.Loading,
        is Resource.Failure -> {
            Image(
                painter = painterResource(Res.drawable.media_file_icon),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                alignment = alignment
            )
        }
        is Resource.Success -> {
            Image(
                painter = resource.value,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                alignment = alignment
            )
        }
    }
}