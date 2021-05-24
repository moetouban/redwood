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
package example.android

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.VERTICAL
import app.cash.treehouse.widget.WidgetDisplay
import example.android.ui.AndroidBox
import example.android.ui.AndroidUiWidgetFactory
import java.io.InputStream
import java.nio.charset.StandardCharsets.UTF_8
import kotlinx.serialization.builtins.serializer

class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val root = LinearLayout(this).apply {
      orientation = VERTICAL
      layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }
    setContentView(root)

    val display = WidgetDisplay(
      root = AndroidBox(root),
      factory = AndroidUiWidgetFactory(this),
    )

    val counterJs = assets.open("presenters.js")
      .use(InputStream::readBytes)
      .toString(UTF_8)

    TreehouseJs(
      js = counterJs,
      polymorphicValueSerializers = {
        subclass(String::class, String.serializer())
        subclass(Boolean::class, Boolean.serializer())
      },
      display = display::apply,
    )
  }
}
