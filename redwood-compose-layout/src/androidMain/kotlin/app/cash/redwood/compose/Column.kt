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

import app.cash.redwood.MeasureSpec.Companion as RedwoodMeasureSpec
import android.content.Context
import android.view.View
import app.cash.redwood.FlexDirection
import app.cash.redwood.FlexboxEngine
import app.cash.redwood.LayoutModifier
import app.cash.redwood.widget.MutableListChildren
import app.cash.redwood.widget.Widget

public class Column(context: Context) : ColumnWidget<View> {
  private val engine = FlexboxEngine().apply {
    flexDirection = FlexDirection.Column
  }

  private val _view = HostView(context)
  override val value: View get() = _view

  private val _children = MutableListChildren(
    onUpdate = { nodes ->
      engine.nodes.clear()
      nodes.forEach { engine.nodes += it.asNode() }
      _view.invalidate()
      _view.requestLayout()
    },
  )
  public val children: Widget.Children<View> get() = _children

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

  private inner class HostView(context: Context) : View(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      val widthSpec = RedwoodMeasureSpec.fromAndroid(widthMeasureSpec)
      val heightSpec = RedwoodMeasureSpec.fromAndroid(heightMeasureSpec)
      engine.measure(widthSpec, heightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      engine.layout(left, top, right, bottom)
    }
  }
}
