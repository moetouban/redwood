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

package example.android.composeui

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import app.cash.redwood.compose.AndroidUiDispatcher.Companion.Main
import app.cash.redwood.protocol.compose.ProtocolRedwoodComposition
import app.cash.redwood.protocol.widget.ProtocolDisplay
import example.presenters.TodoPresenter
import example.schema.compose.DiffProducingTodoWidgetFactory
import example.schema.widget.DiffConsumingTodoWidgetFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class TodoActivity : ComponentActivity() {
  private val scope = CoroutineScope(Main)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val composition = ProtocolRedwoodComposition(
      scope = scope,
      factory = DiffProducingTodoWidgetFactory(),
      widgetVersion = 1U,
      onDiff = { Log.d("RedwoodDiff", it.toString()) },
      onEvent = { Log.d("RedwoodEvent", it.toString()) },
    )

    val root = ComposeUiColumn()
    val composeView = ComposeView(this).apply {
      layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
      setContent(root.value)
    }
    setContentView(composeView)

    val factory = DiffConsumingTodoWidgetFactory(ComposeUiWidgetFactory)
    val display = ProtocolDisplay(
      root = factory.wrap(root),
      factory = factory,
      eventSink = composition,
    )

    composition.start(display)

    composition.setContent {
      TodoPresenter()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    scope.cancel()
  }
}
