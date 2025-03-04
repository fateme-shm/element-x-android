/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.libraries.mediaviewer.api.local

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeAudio
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeImage
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.utils.CommonDrawables
import io.element.android.libraries.designsystem.utils.KeepScreenOn
import io.element.android.libraries.designsystem.utils.OnLifecycleEvent
import io.element.android.libraries.mediaviewer.api.helper.formatFileExtensionAndSize
import io.element.android.libraries.mediaviewer.api.local.exoplayer.ExoPlayerWrapper
import io.element.android.libraries.mediaviewer.api.local.pdf.PdfViewer
import io.element.android.libraries.mediaviewer.api.local.pdf.rememberPdfViewerState
import io.element.android.libraries.ui.strings.CommonStrings
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.ZoomableState
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalMediaView(
    localMedia: LocalMedia?,
    modifier: Modifier = Modifier,
    localMediaViewState: LocalMediaViewState = rememberLocalMediaViewState(),
    mediaInfo: MediaInfo? = localMedia?.info,
) {
    val zoomableState = rememberZoomableState(
        zoomSpec = ZoomSpec(maxZoomFactor = 5f)
    )
    val mimeType = mediaInfo?.mimeType
    when {
        mimeType.isMimeTypeImage() -> MediaImageView(
            localMediaViewState = localMediaViewState,
            localMedia = localMedia,
            zoomableState = zoomableState,
            modifier = modifier
        )
        mimeType.isMimeTypeVideo() -> MediaVideoView(
            localMediaViewState = localMediaViewState,
            localMedia = localMedia,
            modifier = modifier
        )
        mimeType == MimeTypes.Pdf -> MediaPDFView(
            localMediaViewState = localMediaViewState,
            localMedia = localMedia,
            zoomableState = zoomableState,
            modifier = modifier
        )
        // TODO handle audio with exoplayer
        else -> MediaFileView(
            localMediaViewState = localMediaViewState,
            uri = localMedia?.uri,
            info = mediaInfo,
            modifier = modifier
        )
    }
}

@Composable
private fun MediaImageView(
    localMediaViewState: LocalMediaViewState,
    localMedia: LocalMedia?,
    zoomableState: ZoomableState,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Image(
            painter = painterResource(id = CommonDrawables.sample_background),
            modifier = modifier.fillMaxSize(),
            contentDescription = null,
        )
    } else {
        val zoomableImageState = rememberZoomableImageState(zoomableState)
        localMediaViewState.isReady = zoomableImageState.isImageDisplayed
        ZoomableAsyncImage(
            modifier = modifier.fillMaxSize(),
            state = zoomableImageState,
            model = localMedia?.uri,
            contentDescription = stringResource(id = CommonStrings.common_image),
            contentScale = ContentScale.Fit,
        )
    }
}

@UnstableApi
@Composable
private fun MediaVideoView(
    localMediaViewState: LocalMediaViewState,
    localMedia: LocalMedia?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val playerListener = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            localMediaViewState.isReady = true
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            localMediaViewState.isPlaying = isPlaying
        }
    }
    val exoPlayer = remember {
        ExoPlayerWrapper.create(context)
            .apply {
                addListener(playerListener)
                this.prepare()
            }
    }
    if (localMedia?.uri != null) {
        LaunchedEffect(localMedia.uri) {
            val mediaItem = MediaItem.fromUri(localMedia.uri)
            exoPlayer.setMediaItem(mediaItem)
        }
    } else {
        exoPlayer.setMediaItems(emptyList())
    }
    KeepScreenOn(localMediaViewState.isPlaying)
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                setShowPreviousButton(false)
                setShowNextButton(false)
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                controllerShowTimeoutMs = 3000
            }
        },
        modifier = modifier.fillMaxSize()
    )

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> exoPlayer.play()
            Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
            Lifecycle.Event.ON_DESTROY -> {
                exoPlayer.release()
                exoPlayer.removeListener(playerListener)
            }
            else -> Unit
        }
    }
}

@Composable
private fun MediaPDFView(
    localMediaViewState: LocalMediaViewState,
    localMedia: LocalMedia?,
    zoomableState: ZoomableState,
    modifier: Modifier = Modifier,
) {
    val pdfViewerState = rememberPdfViewerState(
        model = localMedia?.uri,
        zoomableState = zoomableState
    )
    localMediaViewState.isReady = pdfViewerState.isLoaded
    PdfViewer(pdfViewerState = pdfViewerState, modifier = modifier)
}

@Composable
private fun MediaFileView(
    localMediaViewState: LocalMediaViewState,
    uri: Uri?,
    info: MediaInfo?,
    modifier: Modifier = Modifier,
) {
    val isAudio = info?.mimeType.isMimeTypeAudio().orFalse()
    localMediaViewState.isReady = uri != null
    Box(modifier = modifier.padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isAudio) Icons.Outlined.GraphicEq else CompoundIcons.Attachment(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(if (isAudio) 0f else -45f),
                )
            }
            if (info != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = info.name,
                    maxLines = 2,
                    style = ElementTheme.typography.fontBodyLgRegular,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatFileExtensionAndSize(info.fileExtension, info.formattedFileSize),
                    style = ElementTheme.typography.fontBodyMdRegular,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
