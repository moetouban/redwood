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
import app.cash.redwood.layout.ColumnWidget
import app.cash.redwood.layout.ComposeColumn
import app.cash.redwood.layout.CrossAxisAlignment
import app.cash.redwood.layout.MainAxisAlignment
import app.cash.redwood.layout.Overflow
import app.cash.redwood.layout.Padding
import app.cash.redwood.layout.RowWidget
import app.cash.redwood.widget.Widget
import example.schema.widget.Column
import example.schema.widget.EmojiSearchWidgetFactory
import example.schema.widget.Row

object AndroidEmojiSearchWidgetFactory : EmojiSearchWidgetFactory<@Composable () -> Unit> {
  override fun Row() = TODO()
  override fun Column() = ColumnBridge(ComposeColumn())
  override fun TextInput() = ComposeUiTextInput()
  override fun Text() = ComposeUiText()
  override fun Image() = ComposeUiImage()
}

class RowBridge<T : Any>(private val delegate: RowWidget<T>) : Row<T>, Widget<T> by delegate {
  override val children get() = delegate.children

  override fun padding(padding: Padding) = delegate.padding(padding)

  override fun overflow(overflow: Overflow) = delegate.overflow(overflow)

  override fun horizontalAlignment(horizontalAlignment: MainAxisAlignment) =
    delegate.horizontalAlignment(horizontalAlignment)

  override fun verticalAlignment(verticalAlignment: CrossAxisAlignment) =
    delegate.verticalAlignment(verticalAlignment)
}

class ColumnBridge<T : Any>(private val delegate: ColumnWidget<T>) : Column<T>, Widget<T> by delegate {
  override val children get() = delegate.children

  override fun padding(padding: Padding) = delegate.padding(padding)

  override fun overflow(overflow: Overflow) = delegate.overflow(overflow)

  override fun horizontalAlignment(horizontalAlignment: CrossAxisAlignment) =
    delegate.horizontalAlignment(horizontalAlignment)

  override fun verticalAlignment(verticalAlignment: MainAxisAlignment) =
    delegate.verticalAlignment(verticalAlignment)
}
