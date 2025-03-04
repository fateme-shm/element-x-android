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

package io.element.android.features.messages.impl.timeline.components.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.element.android.features.messages.impl.timeline.TimelineEvents
import io.element.android.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import io.element.android.features.messages.impl.timeline.di.LocalTimelineItemPresenterFactories
import io.element.android.features.messages.impl.timeline.di.rememberPresenter
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemFileContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemImageContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStateContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.element.android.features.messages.impl.voicemessages.timeline.VoiceMessageState
import io.element.android.libraries.architecture.Presenter

@Composable
fun TimelineItemEventContentView(
    content: TimelineItemEventContent,
    onLinkClicked: (url: String) -> Unit,
    eventSink: (TimelineEvents.EventFromTimelineItem) -> Unit,
    modifier: Modifier = Modifier,
    onContentLayoutChanged: (ContentAvoidingLayoutData) -> Unit = {},
) {
    val presenterFactories = LocalTimelineItemPresenterFactories.current
    when (content) {
        is TimelineItemEncryptedContent -> TimelineItemEncryptedView(
            content = content,
            onContentLayoutChanged = onContentLayoutChanged,
            modifier = modifier
        )
        is TimelineItemRedactedContent -> TimelineItemRedactedView(
            content = content,
            onContentLayoutChanged = onContentLayoutChanged,
            modifier = modifier
        )
        is TimelineItemTextBasedContent -> TimelineItemTextView(
            content = content,
            modifier = modifier,
            onLinkClicked = onLinkClicked,
            onContentLayoutChanged = onContentLayoutChanged
        )
        is TimelineItemUnknownContent -> TimelineItemUnknownView(
            content = content,
            onContentLayoutChanged = onContentLayoutChanged,
            modifier = modifier
        )
        is TimelineItemLocationContent -> TimelineItemLocationView(
            content = content,
            modifier = modifier
        )
        is TimelineItemImageContent -> TimelineItemImageView(
            content = content,
            modifier = modifier,
        )
        is TimelineItemStickerContent -> TimelineItemStickerView(
            content = content,
            modifier = modifier,
        )
        is TimelineItemVideoContent -> TimelineItemVideoView(
            content = content,
            modifier = modifier
        )
        is TimelineItemFileContent -> TimelineItemFileView(
            content = content,
            onContentLayoutChanged = onContentLayoutChanged,
            modifier = modifier
        )
        is TimelineItemAudioContent -> TimelineItemAudioView(
            content = content,
            onContentLayoutChanged = onContentLayoutChanged,
            modifier = modifier
        )
        is TimelineItemStateContent -> TimelineItemStateView(
            content = content,
            modifier = modifier
        )
        is TimelineItemPollContent -> TimelineItemPollView(
            content = content,
            eventSink = eventSink,
            modifier = modifier,
        )
        is TimelineItemVoiceContent -> {
            val presenter: Presenter<VoiceMessageState> = presenterFactories.rememberPresenter(content)
            TimelineItemVoiceView(
                state = presenter.present(),
                content = content,
                onContentLayoutChanged = onContentLayoutChanged,
                modifier = modifier
            )
        }
    }
}
