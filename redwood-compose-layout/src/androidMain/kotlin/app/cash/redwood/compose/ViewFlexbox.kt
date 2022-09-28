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

import app.cash.redwood.flexbox.MeasureSpec.Companion as RedwoodMeasureSpec
import android.content.Context
import android.view.View
import android.widget.ScrollView
import app.cash.redwood.LayoutModifier
import app.cash.redwood.flexbox.FlexDirection
import app.cash.redwood.flexbox.FlexboxEngine
import app.cash.redwood.widget.MutableListChildren
import app.cash.redwood.widget.Widget

public class ViewColumn(context: Context) : ColumnWidget<View> {
  private val engine = FlexboxEngine().apply {
    flexDirection = FlexDirection.Column
  }
  private val view = HostView(context)

  override val children: Widget.Children<View> = MutableListChildren(
    onUpdate = { views ->
      engine.nodes.clear()
      views.forEach { engine.nodes += it.asNode() }
      invalidate()
    },
  )

  override val value: View get() = view

  override var layoutModifiers: LayoutModifier = LayoutModifier

  override fun padding(padding: Padding) {
    engine.padding = padding.toSpacing()
    invalidate()
  }

  override fun overflow(overflow: Overflow) {
    if (overflow == Overflow.Scroll) {
      view.setOnTouchListener { _, _ -> true }
    } else {
      view.setOnTouchListener(null)
    }
    invalidate()
  }

  override fun horizontalAlignment(horizontalAlignment: CrossAxisAlignment) {
    engine.alignItems = horizontalAlignment.toAlignItems()
    invalidate()
  }

  override fun verticalAlignment(verticalAlignment: MainAxisAlignment) {
    engine.justifyContent = verticalAlignment.toJustifyContent()
    invalidate()
  }

  private fun invalidate() {
    view.invalidate()
    view.requestLayout()
  }

  private inner class HostView(context: Context) : ScrollView(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      val widthSpec = RedwoodMeasureSpec.fromAndroid(widthMeasureSpec)
      val heightSpec = RedwoodMeasureSpec.fromAndroid(heightMeasureSpec)
      val (width, height) = engine.measure(widthSpec, heightSpec)
      setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      engine.layout(left, top, right, bottom)
    }
  }
}
