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
package example.shared

import androidx.compose.runtime.BroadcastFrameClock
import app.cash.treehouse.compose.TreehouseComposition
import app.cash.treehouse.protocol.Event
import app.cash.treehouse.widget.WidgetDisplay
import example.sunspot.SunspotBox
import example.sunspot.SunspotButton
import example.sunspot.SunspotText
import example.sunspot.test.SchemaSunspotBox
import example.sunspot.test.SchemaSunspotWidgetFactory
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.test.Test
import kotlin.test.assertEquals

class CounterTest {
  @Test fun basic() = runTest {
    val root = SchemaSunspotBox()
    val display = WidgetDisplay(
      root = root,
      factory = SchemaSunspotWidgetFactory,
    )

    val clock = BroadcastFrameClock()
    lateinit var events: (Event) -> Unit
    val composition = TreehouseComposition(
      scope = this + clock,
      diffs = { diff ->
        println(diff)
        display.apply(diff, events)
      }
    )

    events = { event ->
      println(event)
      composition.sendEvent(event)
    }

    composition.setContent {
      Counter()
    }

    val tick = launch(start = UNDISPATCHED) { clock.withFrameNanos { } }
    clock.sendFrame(0)
    tick.join()

    assertEquals(
      SunspotBox(
        listOf(
          SunspotButton("-1"),
          SunspotText("0"),
          SunspotButton("+1")
        )
      ),
      root.tree
    )

    val tick2 = launch(start = UNDISPATCHED) { clock.withFrameNanos { } }
    clock.sendFrame(0)
    tick2.join()

    assertEquals(
      SunspotBox(
        listOf(
          SunspotButton("-1"),
          SunspotText("-1"),
          SunspotButton("+1")
        )
      ),
      root.tree
    )

    composition.cancel()
  }
}
