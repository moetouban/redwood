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

@file:JvmName("androidUtils") // Avoid name conflicts.

package app.cash.redwood.compose

import android.view.View
import app.cash.redwood.AlignSelf
import app.cash.redwood.MeasureSpec
import app.cash.redwood.MeasureSpecMode
import app.cash.redwood.Node
import app.cash.redwood.Node.Companion.DefaultFlexBasisPercent
import app.cash.redwood.Node.Companion.DefaultFlexGrow
import app.cash.redwood.Node.Companion.DefaultFlexShrink
import app.cash.redwood.Node.Companion.DefaultOrder
import app.cash.redwood.Spacing

internal fun MeasureSpec.Companion.fromAndroid(measureSpec: Int): MeasureSpec = from(
  size = View.MeasureSpec.getSize(measureSpec),
  mode = MeasureSpecMode.fromAndroid(View.MeasureSpec.getMode(measureSpec))
)

internal fun MeasureSpec.toAndroid(): Int = View.MeasureSpec.makeMeasureSpec(size, mode.toAndroid())

internal fun MeasureSpecMode.Companion.fromAndroid(mode: Int): MeasureSpecMode = when (mode) {
  View.MeasureSpec.UNSPECIFIED -> Unspecified
  View.MeasureSpec.EXACTLY -> Exactly
  View.MeasureSpec.AT_MOST -> AtMost
  else -> throw AssertionError()
}

internal fun MeasureSpecMode.toAndroid(): Int = when (this) {
  MeasureSpecMode.Unspecified -> View.MeasureSpec.UNSPECIFIED
  MeasureSpecMode.Exactly -> View.MeasureSpec.EXACTLY
  MeasureSpecMode.AtMost -> View.MeasureSpec.AT_MOST
  else -> throw AssertionError()
}

internal fun View.asNode(): Node = ViewNode(this)

private class ViewNode(private val view: View) : Node {
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
  override val order = DefaultOrder
  override val visible = true
  override val wrapBefore = false

  override fun measure(widthSpec: MeasureSpec, heightSpec: MeasureSpec) {
    view.measure(widthSpec.toAndroid(), heightSpec.toAndroid())
  }

  override fun layout(left: Int, top: Int, right: Int, bottom: Int) {
    view.layout(left, top, right, bottom)
  }
}
