/*
 * Copyright (c) 2024 New Vector Ltd
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

package io.element.android.features.messages.impl.typing

import androidx.compose.runtime.Composable
import io.element.android.features.messages.impl.MessagesView
import io.element.android.features.messages.impl.aMessagesState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

@PreviewsDayNight
@Composable
internal fun MessagesViewWithTypingPreview() = ElementPreview {
    MessagesView(
        state = aMessagesState().copy(
            typingNotificationState = aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(displayName = "Alice"),
                    aTypingRoomMember(displayName = "Bob"),
                ),
            ),
        ),
        onBackPressed = {},
        onRoomDetailsClicked = {},
        onEventClicked = { false },
        onPreviewAttachments = {},
        onUserDataClicked = {},
        onSendLocationClicked = {},
        onCreatePollClicked = {},
        onJoinCallClicked = {},
    )
}
