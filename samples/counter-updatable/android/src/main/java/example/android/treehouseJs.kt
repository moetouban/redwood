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

import android.os.Handler
import android.os.Looper
import android.util.Log
import app.cash.treehouse.protocol.Diff
import app.cash.treehouse.protocol.Event
import com.squareup.duktape.Duktape
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

fun TreehouseJs(
  js: String,
  polymorphicValueSerializers: PolymorphicModuleBuilder<Any>.() -> Unit = {},
  display: (Diff, (Event) -> Unit) -> Unit,
) {
  val presenterJson = Json {
    useArrayPolymorphism = true
    serializersModule = SerializersModule {
      polymorphic(Any::class) {
        polymorphicValueSerializers()
      }
    }
  }

  val duktape = Duktape.create()

  duktape.evaluate("""
      var timeoutCallbacks = [];
      function setTimeout(callback, delay) {
        var insertIndex = timeoutCallbacks.length;
        timeoutCallbacks.push(callback);
        KotlinSetTimeout.schedule(insertIndex, delay);
      };
      function clearTimeout(callback) {
        for (i = 0; i < timeoutCallbacks.length; i++) {
          if (callback === timeoutCallbacks[i]) {
            timeoutCallbacks[i] = function() {};
          }
        }
      }
    """.trimIndent())

  val handler = Handler(Looper.getMainLooper())
  duktape.set("KotlinSetTimeout", KotlinSetTimeout::class.java, object : KotlinSetTimeout {
    override fun schedule(id: Int, delay: Int) {
      handler.postDelayed({
        duktape.evaluate("timeoutCallbacks[$id](); timeoutCallbacks[$id] = null;")
      }, delay.toLong())
    }
  })

  duktape.set("KotlinConsole", KotlinConsole::class.java, object : KotlinConsole {
    override fun error(e: String) {
      Log.e("JavaScript", e)
    }
  })
  duktape.evaluate("var console = { error: function(e) { KotlinConsole.error(e.toString()) } };")


  duktape.evaluate(js)

  lateinit var eventSink: (Event) -> Unit
  duktape.set("KotlinDisplay", KotlinDisplay::class.java, object : KotlinDisplay {
    override fun onDiff(diffJson: String) {
      val diff = presenterJson.decodeFromString(Diff.serializer(), diffJson)
      display(diff, eventSink)
    }
  })

  duktape.evaluate("var events = { onEvent: presenters.example.presenter.runCounter(function(diff) { KotlinDisplay.onDiff(diff) }) };")

  val eventJsonSink = duktape.get("events", JsEvents::class.java)
  eventSink = { event: Event ->
    val eventJson = presenterJson.encodeToString(Event.serializer(), event)
    eventJsonSink.onEvent(eventJson)
  }
}

private interface KotlinSetTimeout {
  fun schedule(id: Int, delay: Int)
}

private interface KotlinConsole {
  fun error(e: String)
}

private interface KotlinDisplay {
  fun onDiff(diffJson: String)
}

private interface JsEvents {
  fun onEvent(eventJson: String)
}
