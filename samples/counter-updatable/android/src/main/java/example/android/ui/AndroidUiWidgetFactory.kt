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
package example.android.ui

import android.content.Context
import android.view.View
import android.widget.Button as AndroidButton
import android.widget.LinearLayout
import android.widget.TextView
import example.ui.widget.Box
import example.ui.widget.Button
import example.ui.widget.Text
import example.ui.widget.UiWidgetFactory

class AndroidUiWidgetFactory(
  private val context: Context,
) : UiWidgetFactory<View> {
  override fun Box(): Box<View> {
    val view = LinearLayout(context)
    return AndroidBox(view)
  }

  override fun Text(): Text<View> {
    val view = TextView(context)
    return AndroidText(view)
  }

  override fun Button(): Button<View> {
    val view = AndroidButton(context)
    return AndroidButton(view)
  }
}
