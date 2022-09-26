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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import app.cash.redwood.FlexDirection
import app.cash.redwood.FlexboxEngine
import app.cash.redwood.LayoutModifier
import app.cash.redwood.Node

private class ComposeFlexbox : ColumnWidget<@Composable () -> Unit> {
  private val engine = FlexboxEngine()

  var direction: FlexDirection
    get() = engine.flexDirection
    set(value) {
      engine.flexDirection = value
    }

  override var padding: Padding
    get() = engine.padding.toPadding()
    set(value) {
      engine.padding = value.toSpacing()
    }

  override var overflow: Overflow = Overflow.Clip

  override var horizontalAlignment: CrossAxisAlignment
    get() = engine.alignItems.toCrossAxisAlignment()
    set(value) {
      engine.alignItems = value.toAlignItems()
    }

  override var verticalAlignment: MainAxisAlignment
    get() = engine.justifyContent.toMainAxisAlignment()
    set(value) {
      engine.justifyContent = value.toJustifyContent()
    }

  override var layoutModifiers: LayoutModifier = LayoutModifier

  override val value = @Composable {
    val modifier = if (overflow == Overflow.Scroll) {
      when (direction) {
        FlexDirection.Row, FlexDirection.RowReverse -> {
          Modifier.horizontalScroll(rememberScrollState())
        }
        FlexDirection.Column, FlexDirection.ColumnReverse -> {
          Modifier.verticalScroll(rememberScrollState())
        }
        else -> throw AssertionError()
      }
    } else {
      Modifier
    }

    Layout(
      modifier = modifier,
      measurePolicy = { measurables, constraints ->
        engine.nodes.clear()
        val composeNodes = measurables.map { measurable ->
          ComposeNode().also { engine.nodes += it.node }
        }
        val (widthSpec, heightSpec) = constraints.toMeasureSpecs()
        val (width, height) = engine.measure(widthSpec, heightSpec)
        layout(width, height) {
          placeable.placeRelative(0, 0)
        }
      }
    )
  }

  class ComposeNode {
    val node = Node()
    var placeable: Placeable? = null

    init {
        node.measure =
    }
  }
}

