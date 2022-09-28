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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.cash.redwood.LayoutModifier
import app.cash.redwood.treehouse.TreehouseApp
import app.cash.redwood.treehouse.TreehouseView
import app.cash.redwood.treehouse.ZiplineTreehouseUi
import app.cash.redwood.treehouse.composeui.TreehouseComposeView
import example.schema.widget.LazyColumn
import example.values.IntervalList
import example.values.IntervalListLazyListIntervalContentWrapper
import example.values.LazyListIntervalContent
import example.values.MutableIntervalList

class ComposeUiLazyColumn<T : Any>(
  treehouseApp: TreehouseApp<T>,
  widgetFactory: AndroidEmojiSearchWidgetFactory<T>,
) : LazyColumn<@Composable () -> Unit> {
  private var intervals by mutableStateOf<IntervalList<LazyListIntervalContent>>(MutableIntervalList())

  override var layoutModifiers: LayoutModifier = LayoutModifier

  override fun lazyListIntervalContents(lazyListIntervalContents: IntervalListLazyListIntervalContentWrapper) {
    this.intervals = lazyListIntervalContents.value
  }

  override val value = @Composable {
    LazyColumn(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth(),
    ) {
      itemsIndexed(intervals) { _, localIntervalIndex, interval ->
        val item = interval.value.item
        var shown by remember { mutableStateOf(false) }
        if (!shown) Text("Waiting…", Modifier.size(64.dp))
        AndroidView(
          factory = {
            TreehouseComposeView(it, treehouseApp, widgetFactory)
          },
          update = {
            it.setContent(
              object : TreehouseView.Content<T> {
                override fun get(app: T): ZiplineTreehouseUi {
                  shown = true
                  return item.get(localIntervalIndex)
                }
              },
            )
          },
        )
      }
    }
  }
}

private fun <T> localIntervalIndex(index: Int, interval: IntervalList.Interval<T>) = index - interval.startIndex

private inline fun <T> LazyListScope.itemsIndexed(
  intervals: IntervalList<T>,
  crossinline itemContent: @Composable LazyItemScope.(index: Int, localIntervalIndex: Int, interval: IntervalList.Interval<T>) -> Unit,
) {
  items(
    count = intervals.size,
  ) {
    val interval = intervals[it]
    itemContent(it, localIntervalIndex(it, interval), interval)
  }
}
