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
package app.cash.zipline.samples.emojisearch

import app.cash.zipline.loader.ManifestVerifier
import example.schema.widget.DiffConsumingEmojiSearchWidgetFactory
import example.schema.widget.EmojiSearchWidgetFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import platform.Foundation.NSURLSession
import platform.UIKit.UIView
import app.cash.redwood.treehouse.*
import app.cash.redwood.protocol.widget.*
import app.cash.redwood.treehouse.TreehouseApp

class EmojiSearchZipline(
  private val nsurlSession: NSURLSession,
  private val hostApi: HostApi,
  private val widgetFactory: EmojiSearchWidgetFactory<UIView>,
) {
  private val coroutineScope: CoroutineScope = MainScope()
  private val manifestUrl = "http://localhost:8080/manifest.zipline.json"

  public fun createTreehouseApp(): TreehouseApp<EmojiSearchPresenter> {
    val treehouseLauncher = app.cash.redwood.treehouse.TreehouseLauncher(
      httpClient = URLSessionZiplineHttpClient(nsurlSession),
      manifestVerifier = ManifestVerifier.Companion.NO_SIGNATURE_CHECKS,
    )

    return treehouseLauncher.launch(
      scope = coroutineScope,
      spec = EmojiSearchAppSpec(
        manifestUrlString = manifestUrl,
        hostApi = hostApi,
        viewBinderAdapter = object : ViewBinder.Adapter {
          override fun beforeUpdatedCode(view: TreehouseView<*>) {
            view.protocolDisplayRoot.children(0)!!.clear()
          }

          override fun widgetFactory(
            view: TreehouseView<*>,
            json: Json,
          ): DiffConsumingWidget.Factory<*> = DiffConsumingEmojiSearchWidgetFactory<UIView>(
            delegate = widgetFactory,
            json = json,
          )
        },
      ),
    )
  }
}
