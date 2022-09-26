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

package app.cash.redwood.compose

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.unit.Constraints
import app.cash.redwood.AlignSelf
import app.cash.redwood.MeasureSpec
import app.cash.redwood.Node
import app.cash.redwood.Node.Companion.DefaultFlexBasisPercent
import app.cash.redwood.Node.Companion.DefaultFlexGrow
import app.cash.redwood.Node.Companion.DefaultFlexShrink
import app.cash.redwood.Spacing

internal fun Constraints.toMeasureSpecs(): Pair<MeasureSpec, MeasureSpec> {

}

private class MeasurableNode(private val measurable: Measurable) : Node {
  override val alignSelf = AlignSelf.Auto
  override val baseline = -1
  override val flexBasisPercent = DefaultFlexBasisPercent
  override val flexGrow = DefaultFlexGrow
  override val flexShrink = DefaultFlexShrink
  override val width get() = view.width
  override val height get() = view.height
  override val margin = Spacing.Zero
  override val maxHeight = Int.MAX_VALUE
  override val maxWidth = Int.MAX_VALUE
  override val measuredWidth get() = view.measuredWidth
  override val measuredHeight get() = view.measuredHeight
  override val minWidth get() = view.minimumWidth
  override val minHeight get() = view.minimumHeight
  override val order = Node.DefaultOrder
  override val visible = true
  override val wrapBefore = false
}
