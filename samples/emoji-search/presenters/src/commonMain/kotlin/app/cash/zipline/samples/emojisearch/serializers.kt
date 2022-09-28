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

import androidx.compose.runtime.collection.MutableVector
import app.cash.zipline.ziplineServiceSerializer
import example.values.IntervalList
import example.values.LazyListIntervalContent
import example.values.MutableIntervalList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val treehouseSerializersModule = SerializersModule {
  contextual(ziplineServiceSerializer<LazyListIntervalContent.Item>())
  contextual(MutableVectorLazyListIntervalContentSerializer)
  polymorphic(IntervalList::class) {
    subclass(MutableIntervalListSerializer)
  }
}

object MutableVectorLazyListIntervalContentSerializer : KSerializer<MutableVector<LazyListIntervalContent>> {
  private val serializer = ListSerializer(LazyListIntervalContent.serializer())

  override val descriptor: SerialDescriptor get() = serializer.descriptor

  override fun deserialize(decoder: Decoder): MutableVector<LazyListIntervalContent> {
    val content = serializer.deserialize(decoder)
    return MutableVector(content.size) { content[it] }
  }

  override fun serialize(encoder: Encoder, value: MutableVector<LazyListIntervalContent>) {
    val content = List(value.size) { value[it] }
    serializer.serialize(encoder, content)
  }
}

object MutableVectorIntervalLazyListIntervalContentSerializer : KSerializer<MutableVector<IntervalList.Interval<LazyListIntervalContent>>> {
  private val serializer = ListSerializer(IntervalList.Interval.serializer(LazyListIntervalContent.serializer()))

  override val descriptor: SerialDescriptor get() = serializer.descriptor

  override fun deserialize(decoder: Decoder): MutableVector<IntervalList.Interval<LazyListIntervalContent>> {
    val content = serializer.deserialize(decoder)
    return MutableVector(content.size) { content[it] }
  }

  override fun serialize(encoder: Encoder, value: MutableVector<IntervalList.Interval<LazyListIntervalContent>>) {
    val content = List(value.size) { value[it] }
    serializer.serialize(encoder, content)
  }
}

object MutableIntervalListSerializer : KSerializer<MutableIntervalList<LazyListIntervalContent>> {
  private val serializer = MutableVectorIntervalLazyListIntervalContentSerializer

  override val descriptor: SerialDescriptor get() = serializer.descriptor

  override fun deserialize(decoder: Decoder): MutableIntervalList<LazyListIntervalContent> {
    val content = serializer.deserialize(decoder)
    val mutableIntervalList = MutableIntervalList<LazyListIntervalContent>()
    content.forEach {
      mutableIntervalList.addInterval(it.size, it.value)
    }
    return mutableIntervalList
  }

  override fun serialize(encoder: Encoder, value: MutableIntervalList<LazyListIntervalContent>) {
    val content = value.intervals
    serializer.serialize(encoder, content)
  }
}
