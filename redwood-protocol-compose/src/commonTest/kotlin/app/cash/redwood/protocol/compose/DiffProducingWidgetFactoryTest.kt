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
package app.cash.redwood.protocol.compose

import app.cash.redwood.LayoutModifier
import app.cash.redwood.protocol.Diff
import app.cash.redwood.protocol.Event
import app.cash.redwood.protocol.LayoutModifiers
import app.cash.redwood.protocol.PropertyDiff
import example.redwood.compose.DiffProducingExampleSchemaWidgetFactory
import example.redwood.compose.customType
import example.redwood.compose.customTypeWithDefault
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.modules.SerializersModule

class DiffProducingWidgetFactoryTest {
  @Test fun propertyUsesSerializersModule() {
    val json = Json {
      serializersModule = SerializersModule {
        contextual(Duration::class, DurationIsoSerializer)
      }
    }
    val factory = DiffProducingExampleSchemaWidgetFactory(json)
    val textInput = factory.TextInput()

    val diffProducingWidget = textInput as AbstractDiffProducingWidget
    val diffSink = RecordingDiffSink()
    val diffAppender = DiffAppender(diffSink)
    diffProducingWidget.id = 1L
    diffProducingWidget._diffAppender = diffAppender

    textInput.customType(10.seconds)
    diffAppender.trySend()

    val expected = Diff(
      propertyDiffs = listOf(
        PropertyDiff(1L, 2, JsonPrimitive("PT10S")),
      ),
    )
    assertEquals(expected, diffSink.diffs.single())
  }

  @Test fun layoutModifierUsesSerializersModule() {
    val json = Json {
      serializersModule = SerializersModule {
        contextual(Duration::class, DurationIsoSerializer)
      }
    }
    val factory = DiffProducingExampleSchemaWidgetFactory(json)
    val button = factory.Button()

    val diffProducingWidget = button as AbstractDiffProducingWidget
    val diffSink = RecordingDiffSink()
    val diffAppender = DiffAppender(diffSink)
    diffProducingWidget.id = 1L
    diffProducingWidget._diffAppender = diffAppender

    button.layoutModifiers = LayoutModifier.customType(10.seconds)
    diffAppender.trySend()

    val expected = Diff(
      layoutModifiers = listOf(
        LayoutModifiers(
          1L,
          buildJsonArray {
            add(
              buildJsonArray {
                add(JsonPrimitive(3))
                add(
                  buildJsonObject {
                    put("customType", JsonPrimitive("PT10S"))
                  },
                )
              },
            )
          },
        ),
      ),
    )
    assertEquals(expected, diffSink.diffs.single())
  }

  @Test fun layoutModifierDefaultValueNotSerialized() {
    val json = Json {
      serializersModule = SerializersModule {
        contextual(Duration::class, DurationIsoSerializer)
      }
    }
    val factory = DiffProducingExampleSchemaWidgetFactory(json)
    val button = factory.Button()

    val diffProducingWidget = button as AbstractDiffProducingWidget
    val diffSink = RecordingDiffSink()
    val diffAppender = DiffAppender(diffSink)
    diffProducingWidget.id = 1L
    diffProducingWidget._diffAppender = diffAppender

    button.layoutModifiers = LayoutModifier.customTypeWithDefault(10.seconds, "sup")
    diffAppender.trySend()

    val expected = Diff(
      layoutModifiers = listOf(
        LayoutModifiers(
          1L,
          buildJsonArray {
            add(
              buildJsonArray {
                add(JsonPrimitive(5))
                add(
                  buildJsonObject {
                    put("customType", JsonPrimitive("PT10S"))
                  },
                )
              },
            )
          },
        ),
      ),
    )
    assertEquals(expected, diffSink.diffs.single())
  }

  @Test fun eventUsesSerializersModule() {
    val json = Json {
      serializersModule = SerializersModule {
        contextual(Duration::class, DurationIsoSerializer)
      }
    }
    val factory = DiffProducingExampleSchemaWidgetFactory(json)
    val textInput = factory.TextInput()

    val diffProducingWidget = textInput as AbstractDiffProducingWidget
    diffProducingWidget._diffAppender = DiffAppender(RecordingDiffSink())

    var argument: Duration? = null
    textInput.onChangeCustomType {
      argument = it
    }

    diffProducingWidget.sendEvent(Event(1L, 4, JsonPrimitive("PT10S")))

    assertEquals(10.seconds, argument)
  }

  @Test fun unknownEventThrowsDefault() {
    val factory = DiffProducingExampleSchemaWidgetFactory()
    val button = factory.Button() as AbstractDiffProducingWidget

    val event = Event(1L, 3456543)
    val t = assertFailsWith<IllegalArgumentException> {
      button.sendEvent(event)
    }

    assertEquals("Unknown event tag 3456543 for widget kind 4", t.message)
  }

  @Test fun unknownEventCallsHandler() {
    val handler = RecordingProtocolMismatchHandler()
    val factory = DiffProducingExampleSchemaWidgetFactory(mismatchHandler = handler)
    val button = factory.Button() as AbstractDiffProducingWidget

    button.sendEvent(Event(1L, 3456543))

    assertEquals("Unknown event 3456543 for 4", handler.events.single())
  }
}
