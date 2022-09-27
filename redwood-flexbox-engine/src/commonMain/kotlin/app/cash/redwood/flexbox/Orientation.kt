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
package app.cash.redwood.flexbox

internal fun FlexDirection.toOrientation(): Orientation {
  if (this == FlexDirection.Row || this == FlexDirection.RowReverse) {
    return Orientation.Horizontal
  } else {
    return Orientation.Vertical
  }
}

/**
 * An interface to perform operations along the main/cross axis without knowledge
 * of the underlying [FlexDirection].
 */
internal sealed interface Orientation {
  fun mainPaddingStart(padding: Spacing): Int
  fun mainPaddingEnd(padding: Spacing): Int
  fun crossPaddingStart(padding: Spacing): Int
  fun crossPaddingEnd(padding: Spacing): Int

  fun mainSize(item: FlexItem): Int
  fun crossSize(item: FlexItem): Int
  fun mainMeasuredSize(item: FlexItem): Int
  fun crossMeasuredSize(item: FlexItem): Int

  fun mainMarginStart(item: FlexItem): Int
  fun mainMarginEnd(item: FlexItem): Int
  fun crossMarginStart(item: FlexItem): Int
  fun crossMarginEnd(item: FlexItem): Int

  object Horizontal : Orientation {
    override fun mainPaddingStart(padding: Spacing) = padding.start
    override fun mainPaddingEnd(padding: Spacing) = padding.end
    override fun crossPaddingStart(padding: Spacing) = padding.top
    override fun crossPaddingEnd(padding: Spacing) = padding.bottom
    override fun mainSize(item: FlexItem) = item.measurable.width
    override fun crossSize(item: FlexItem) = item.measurable.height
    override fun mainMeasuredSize(item: FlexItem) = item.measuredWidth
    override fun crossMeasuredSize(item: FlexItem) = item.measuredHeight
    override fun mainMarginStart(item: FlexItem) = item.margin.start
    override fun mainMarginEnd(item: FlexItem) = item.margin.end
    override fun crossMarginStart(item: FlexItem) = item.margin.top
    override fun crossMarginEnd(item: FlexItem) = item.margin.bottom
  }

  object Vertical : Orientation {
    override fun mainPaddingStart(padding: Spacing) = padding.top
    override fun mainPaddingEnd(padding: Spacing) = padding.bottom
    override fun crossPaddingStart(padding: Spacing) = padding.start
    override fun crossPaddingEnd(padding: Spacing) = padding.end
    override fun mainSize(item: FlexItem) = item.measurable.height
    override fun crossSize(item: FlexItem) = item.measurable.width
    override fun mainMeasuredSize(item: FlexItem) = item.measuredHeight
    override fun crossMeasuredSize(item: FlexItem) = item.measuredWidth
    override fun mainMarginStart(item: FlexItem) = item.margin.top
    override fun mainMarginEnd(item: FlexItem) = item.margin.bottom
    override fun crossMarginStart(item: FlexItem) = item.margin.start
    override fun crossMarginEnd(item: FlexItem) = item.margin.end
  }
}
