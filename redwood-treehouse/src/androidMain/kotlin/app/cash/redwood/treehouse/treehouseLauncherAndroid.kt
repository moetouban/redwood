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
package app.cash.redwood.treehouse

import android.content.Context
import android.os.Looper
import android.util.Log
import android.view.View
import app.cash.redwood.LayoutModifier
import app.cash.redwood.protocol.ChildrenDiff.Companion.RootChildrenTag
import app.cash.redwood.protocol.EventSink
import app.cash.redwood.protocol.PropertyDiff
import app.cash.redwood.protocol.widget.DiffConsumingWidget
import app.cash.redwood.widget.ViewGroupChildren
import app.cash.zipline.loader.ManifestVerifier
import app.cash.zipline.loader.ZiplineCache
import app.cash.zipline.loader.asZiplineHttpClient
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.serialization.json.JsonArray
import okhttp3.OkHttpClient
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath

public fun TreehouseLauncher(
  context: Context,
  httpClient: OkHttpClient,
  manifestVerifier: ManifestVerifier,
  embeddedDir: Path = "/".toPath(),
  embeddedFileSystem: FileSystem = FileSystem.SYSTEM,
  cacheName: String = "zipline",
  cacheMaxSizeInBytes: Long = 50L * 1024L * 1024L,
): TreehouseLauncher = TreehouseLauncher(
  platform = AndroidTreehousePlatform(context),
  dispatchers = AndroidTreehouseDispatchers(),
  httpClient = httpClient.asZiplineHttpClient(),
  manifestVerifier = manifestVerifier,
  embeddedDir = embeddedDir,
  embeddedFileSystem = embeddedFileSystem,
  cacheName = cacheName,
  cacheMaxSizeInBytes = cacheMaxSizeInBytes,
)

internal class AndroidTreehousePlatform(
  private val context: Context,
) : TreehousePlatform {
  override fun logInfo(message: String, throwable: Throwable?) {
    Log.i("Zipline", message, throwable)
  }

  override fun logWarning(message: String, throwable: Throwable?) {
    Log.w("Zipline", message, throwable)
  }

  override fun newCache(name: String, maxSizeInBytes: Long) = ZiplineCache(
    context = context,
    fileSystem = FileSystem.SYSTEM,
    directory = context.cacheDir.toOkioPath() / name,
    maxSizeInBytes = maxSizeInBytes,
  )
}

/**
 * Implements [TreehouseDispatchers] suitable for production Android use. This creates a background
 * thread for all Zipline work.
 */
internal class AndroidTreehouseDispatchers : TreehouseDispatchers {
  private lateinit var ziplineThread: Thread

  /** The single thread that runs all JavaScript. We only have one QuickJS instance at a time. */
  private val executorService = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "Treehouse")
      .also { ziplineThread = it }
  }

  override val ui: CoroutineDispatcher get() = Dispatchers.Main
  override val zipline: CoroutineDispatcher = executorService.asCoroutineDispatcher()

  override fun checkUi() {
    check(Looper.myLooper() == Looper.getMainLooper())
  }

  override fun checkZipline() {
    check(Thread.currentThread() == ziplineThread)
  }
}

internal class ProtocolDisplayRoot(
  override val value: TreehouseWidgetView<*>,
) : DiffConsumingWidget<View> {
  private val children = ViewGroupChildren(value)

  override var layoutModifiers: LayoutModifier = LayoutModifier

  override fun updateLayoutModifier(value: JsonArray) {
  }

  override fun apply(diff: PropertyDiff, eventSink: EventSink) {
    error("unexpected update on view root: $diff")
  }

  override fun children(tag: Int) = when (tag) {
    RootChildrenTag -> children
    else -> error("unexpected tag: $tag")
  }
}
