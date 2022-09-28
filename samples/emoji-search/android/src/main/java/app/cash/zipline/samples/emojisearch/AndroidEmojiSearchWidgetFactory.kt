/*
 * Copyright (C) 2022 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.zipline.samples.emojisearch

import androidx.compose.runtime.Composable
import app.cash.redwood.compose.ComposeColumn
import example.schema.widget.Column
import example.schema.widget.EmojiSearchWidgetFactory

object AndroidEmojiSearchWidgetFactory : EmojiSearchWidgetFactory<@Composable () -> Unit> {
  override fun Row() = TODO()
  override fun Column(): Column<@Composable () -> Unit> =
    object : ComposeColumn(), Column<@Composable () -> Unit> {}
  override fun TextInput() = ComposeUiTextInput()
  override fun Text() = ComposeUiText()
  override fun Image() = ComposeUiImage()
}
