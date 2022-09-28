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

import app.cash.redwood.flexbox.Measurable as RedwoodMeasurable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import app.cash.redwood.LayoutModifier
import app.cash.redwood.flexbox.FlexDirection
import app.cash.redwood.flexbox.FlexboxEngine
import app.cash.redwood.flexbox.MeasureSpec
import app.cash.redwood.flexbox.Node
import app.cash.redwood.flexbox.Size
import app.cash.redwood.widget.Widget
import app.cash.redwood.widget.compose.ComposeWidgetChildren

public open class ComposeColumn : ColumnWidget<@Composable () -> Unit> {
  private val engine = FlexboxEngine().apply {
    flexDirection = FlexDirection.Column
  }

  private val _children = ComposeWidgetChildren()
  override val children: Widget.Children<@Composable () -> Unit> get() = _children

  private var recomposeTick by mutableStateOf(0)
  private var overflow by mutableStateOf(Overflow.Clip)

  override fun padding(padding: Padding) {
    engine.padding = padding.toSpacing()
    recomposeTick++
  }

  override fun overflow(overflow: Overflow) {
    this.overflow = overflow
  }

  override fun horizontalAlignment(horizontalAlignment: CrossAxisAlignment) {
    engine.alignItems = horizontalAlignment.toAlignItems()
    recomposeTick++
  }

  override fun verticalAlignment(verticalAlignment: MainAxisAlignment) {
    engine.justifyContent = verticalAlignment.toJustifyContent()
    recomposeTick++
  }

  override var layoutModifiers: LayoutModifier = LayoutModifier

  override val value: @Composable () -> Unit = {
    // Observe this value so we can manually trigger recomposition.
    recomposeTick

    Layout(
      content = {
        _children.render()
      },
      modifier = if (overflow == Overflow.Scroll) {
        Modifier.verticalScroll(rememberScrollState())
      } else {
        Modifier
      },
      measurePolicy = ::measure,
    )
  }

  private fun measure(
    scope: MeasureScope,
    measurables: List<Measurable>,
    constraints: Constraints,
  ): MeasureResult = with(scope) {
    engine.nodes.clear()
    measurables.forEach { measurable ->
      val node = Node()
      node.measurable = ComposeMeasurable(measurable)
      engine.nodes += node
    }

    val (widthSpec, heightSpec) = constraints.toMeasureSpecs()
    val (width, height) = engine.measure(widthSpec, heightSpec)
    val result = layout(width, height) {
      engine.nodes.forEach { node ->
        node.layout = { left, top, _, _ ->
          (node.measurable as ComposeMeasurable).placeable?.placeRelative(left, top)
        }
      }
      engine.layout(0, 0, width, height)
    }

    // Clear any references to the compose measurable.
    engine.nodes.forEach { node ->
      node.measurable = RedwoodMeasurable()
      node.layout = { _, _, _, _ -> }
    }

    return result
  }
}

private class ComposeMeasurable(val measurable: Measurable) : RedwoodMeasurable() {
  override val minWidth: Int
    get() = measurable.minIntrinsicWidth(0)
  override val minHeight: Int
    get() = measurable.minIntrinsicHeight(0)
  override val maxWidth: Int
    get() = measurable.maxIntrinsicWidth(Int.MAX_VALUE)
  override val maxHeight: Int
    get() = measurable.maxIntrinsicHeight(Int.MAX_VALUE)

  var placeable: Placeable? = null
    private set

  override fun measure(widthSpec: MeasureSpec, heightSpec: MeasureSpec): Size {
    val placeable = measurable.measure(Pair(widthSpec, heightSpec).toConstraints())
    this.placeable = placeable
    return Size(placeable.width, placeable.height)
  }
}
