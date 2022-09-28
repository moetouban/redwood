/*
 * Copyright (C) 2021 Square, Inc.
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
package example.schema

import app.cash.redwood.LayoutModifier
import app.cash.redwood.layout.CrossAxisAlignment
import app.cash.redwood.layout.GrowLayoutModifier
import app.cash.redwood.layout.HorizontalAlignmentLayoutModifier
import app.cash.redwood.layout.MainAxisAlignment
import app.cash.redwood.layout.Overflow
import app.cash.redwood.layout.Padding
import app.cash.redwood.layout.ShrinkLayoutModifier
import app.cash.redwood.layout.VerticalAlignmentLayoutModifier
import app.cash.redwood.schema.Children
import app.cash.redwood.schema.Default
import app.cash.redwood.schema.Property
import app.cash.redwood.schema.Schema
import app.cash.redwood.schema.Widget

@Schema(
  [
    Row::class,
    Column::class,
    TextInput::class,
    Text::class,
    Image::class,
  ],
)
interface EmojiSearch

@Widget(1)
data class Row(
  @Property(1) @Default("Padding.Zero") val padding: Padding,
  @Property(2) @Default("Overflow.Clip") val overflow: Overflow,
  @Property(3) @Default("MainAxisAlignment.Start") val horizontalAlignment: MainAxisAlignment,
  @Property(4) @Default("CrossAxisAlignment.Start") val verticalAlignment: CrossAxisAlignment,
  @Children(5) val children: RowScope.() -> Unit,
)

object RowScope {
  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow */
  fun LayoutModifier.grow(value: Int): LayoutModifier {
    return then(GrowLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink */
  fun LayoutModifier.shrink(value: Int): LayoutModifier {
    return then(ShrinkLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/align-self */
  fun LayoutModifier.verticalAlignment(alignment: CrossAxisAlignment): LayoutModifier {
    return then(VerticalAlignmentLayoutModifier(alignment))
  }
}

@Widget(2)
data class Column(
  @Property(1) @Default("Padding.Zero") val padding: Padding,
  @Property(2) @Default("Overflow.Clip") val overflow: Overflow,
  @Property(3) @Default("CrossAxisAlignment.Start") val horizontalAlignment: CrossAxisAlignment,
  @Property(4) @Default("MainAxisAlignment.Start") val verticalAlignment: MainAxisAlignment,
  @Children(5) val children: ColumnScope.() -> Unit,
)

object ColumnScope {
  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow */
  fun LayoutModifier.grow(value: Int): LayoutModifier {
    return then(GrowLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink */
  fun LayoutModifier.shrink(value: Int): LayoutModifier {
    return then(ShrinkLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/align-self */
  fun LayoutModifier.horizontalAlignment(alignment: CrossAxisAlignment): LayoutModifier {
    return then(HorizontalAlignmentLayoutModifier(alignment))
  }
}

@Widget(3)
data class TextInput(
  @Property(1) val hint: String,
  @Property(2) val text: String,
  @Property(3) val onTextChanged: (String) -> Unit,
)

@Widget(4)
data class Text(
  @Property(1) val text: String,
)

@Widget(5)
data class Image(
  @Property(1) val url: String,
)
