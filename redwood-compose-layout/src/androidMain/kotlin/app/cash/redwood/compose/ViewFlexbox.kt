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

private class ViewFlexbox(context: Context) {
  val engine = FlexboxEngine()
  val view = HostView(context)

  val children = MutableListChildren(
    onUpdate = { views ->
      engine.nodes.clear()
      views.forEach { engine.nodes += it.asNode() }
      view.invalidate()
      view.requestLayout()
    },
  )

  inner class HostView(context: Context) : ScrollView(context) {

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

public class ViewColumnDelegate(context: Context) : Widget<View> {
  private val flexbox = ViewFlexbox(context).apply {
    engine.flexDirection = FlexDirection.Column
  }

  public var padding: Padding
    get() = flexbox.engine.padding.toPadding()
    set(value) {
      flexbox.engine.padding = value.toSpacing()
    }

  public var overflow: Overflow = Overflow.Clip

  public var horizontalAlignment: CrossAxisAlignment
    get() = flexbox.engine.alignItems.toCrossAxisAlignment()
    set(value) {
      flexbox.engine.alignItems = value.toAlignItems()
    }

  public var verticalAlignment: MainAxisAlignment
    get() = flexbox.engine.justifyContent.toMainAxisAlignment()
    set(value) {
      flexbox.engine.justifyContent = value.toJustifyContent()
    }

  override val value: View = flexbox.view

  override var layoutModifiers: LayoutModifier = LayoutModifier
}
