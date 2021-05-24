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
package example.presenter

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.treehouse.compose.TreehouseComposition
import app.cash.treehouse.compose.TreehouseScope
import app.cash.treehouse.protocol.Diff
import app.cash.treehouse.protocol.Event
import example.ui.compose.Button
import example.ui.compose.Text
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
private fun TreehouseScope.Counter(value: Int = 0) {
  var count by remember { mutableStateOf(value) }

  Button("-1", onClick = { count-- })
  Text(count.toString())
  Button("+1", onClick = { count++ })
}

private val presenterJson = Json {
  useArrayPolymorphism = true
  serializersModule = SerializersModule {
    polymorphic(Any::class) {
      subclass(String::class, String.serializer())
      subclass(Boolean::class, Boolean.serializer())
    }
  }
}

@JsExport
@JsName("runCounter")
fun runCounter(diffs: (String) -> Unit): (String) -> Unit {
  val clock = BroadcastFrameClock()
  GlobalScope.launch {
    while (true) {
      delay(100)
      clock.sendFrame(0L)
    }
  }

  val composition = TreehouseComposition(
    scope = GlobalScope + clock,
    display = { diff, events ->
      val diffJson = presenterJson.encodeToString(Diff.serializer(), diff)
      diffs(diffJson)
    },
  )
  composition.setContent {
    Counter()
  }

  return { eventJson ->
    val event = presenterJson.decodeFromString(Event.serializer(), eventJson)
    composition.sendEvent(event)
  }
}
