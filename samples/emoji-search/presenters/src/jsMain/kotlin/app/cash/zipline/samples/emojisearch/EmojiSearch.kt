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
package app.cash.zipline.samples.emojisearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import app.cash.redwood.treehouse.TreehouseUi
import app.cash.redwood.treehouse.ZiplineTreehouseUi
import app.cash.redwood.treehouse.asZiplineTreehouseUi
import app.cash.zipline.samples.emojisearch.EmojiSearchEvent.SearchTermEvent
import example.schema.compose.Column
import example.schema.compose.DiffProducingEmojiSearchWidgetFactory
import example.schema.compose.Image
import example.schema.compose.Text
import example.schema.compose.TextInput
import example.values.IntervalList
import example.values.IntervalListLazyListIntervalContentWrapper
import example.values.LazyListIntervalContent
import example.values.MutableIntervalList
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Contextual
import kotlinx.serialization.json.Json

class LazyImage(private val json: Json, private val images: List<EmojiImage>) : LazyListIntervalContent.Item {

  override fun get(index: Int): ZiplineTreehouseUi {
    val treehouseUi = object : TreehouseUi {
      @Composable
      override fun Show() {
        val image = images[index]
        println("veyndan___ ${image.label}")
        Image(url = image.url)
      }
    }
    return treehouseUi.asZiplineTreehouseUi(DiffProducingEmojiSearchWidgetFactory(json), widgetVersion = 1u)
  }
}

class EmojiSearchTreehouseUi(
  private val initialViewModel: EmojiSearchViewModel,
  private val viewModels: Flow<EmojiSearchViewModel>,
  private val onEvent: (EmojiSearchEvent) -> Unit,
  private val json: Json,
) : TreehouseUi {

  @Composable
  override fun Show() {
    val viewModel = viewModels.collectAsState(initialViewModel).value

    Column {
      TextInput(
        text = viewModel.searchTerm,
        hint = "Search",
        onTextChanged = { onEvent(SearchTermEvent(it)) },
      )
      LazyColumn {
        items(
          viewModel.images.size,
          itemContent = LazyImage(json, viewModel.images),
        )
      }
    }
  }
}

@Composable
fun LazyColumn(content: LazyListScope.() -> Unit) {
  val lazyListScope = LazyListScope()
  content(lazyListScope)
  example.schema.compose.LazyColumn(IntervalListLazyListIntervalContentWrapper(lazyListScope._intervals))
}

class LazyListScope {

  /* private */ @Contextual
  val _intervals = MutableIntervalList<LazyListIntervalContent>()

  @Contextual
  val intervals: IntervalList<LazyListIntervalContent> = _intervals

  fun items(
    count: Int,
    itemContent: LazyListIntervalContent.Item,
  ) {
    _intervals.addInterval(
      count,
      LazyListIntervalContent(
        item = itemContent,
      ),
    )
  }
}
