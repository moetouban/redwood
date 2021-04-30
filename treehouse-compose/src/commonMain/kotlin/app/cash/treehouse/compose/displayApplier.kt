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

package app.cash.treehouse.compose

import androidx.compose.runtime.AbstractApplier
import app.cash.treehouse.protocol.Event
import app.cash.treehouse.protocol.PropertyDiff
import app.cash.treehouse.widget.Widget

private class DisplayChildrenWidget(val tag: Int) : Widget<Any> {
  @Suppress("JoinDeclarationAndAssignment") // https://youtrack.jetbrains.com/issue/KTIJ-10763
  lateinit var children: Widget.Children<Any>

  constructor(children: Widget.Children<Any>) : this(-1) {
    this.children = children
  }

  override val value get() = throw UnsupportedOperationException()

  override fun apply(diff: PropertyDiff, events: (Event) -> Unit) {
    throw UnsupportedOperationException()
  }
}

internal class WidgetApplier(
  root: Widget.Children<Any>,
) : AbstractApplier<Widget<Any>>(DisplayChildrenWidget(root)) {
  override fun insertTopDown(index: Int, instance: Widget<Any>) {
    if (instance is DisplayChildrenWidget) {
      instance.children = current.children(instance.tag)
    } else {
      val current = current as DisplayChildrenWidget
      current.children.insert(index, instance)
    }
  }

  override fun insertBottomUp(index: Int, instance: Widget<Any>) {
    // Ignored, we insert top-down for now.
  }

  override fun remove(index: Int, count: Int) {
    // Children instances are never removed from their parents.
    val current = current as DisplayChildrenWidget
    current.children.remove(index, count)
  }

  override fun move(from: Int, to: Int, count: Int) {
    // Children instances are never moved within their parents.
    val current = current as DisplayChildrenWidget
    current.children.move(from, to, count)
  }

  override fun onClear() {
    // Root instance is always a children instance.
    val current = current as DisplayChildrenWidget
    current.children.clear()
  }
}
