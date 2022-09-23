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
package app.cash.redwood.compose

import app.cash.redwood.AlignItems
import app.cash.redwood.JustifyContent
import app.cash.redwood.Spacing

internal fun MainAxisAlignment.toJustifyContent() = when (this) {
  MainAxisAlignment.Start -> JustifyContent.FlexStart
  MainAxisAlignment.Center -> JustifyContent.Center
  MainAxisAlignment.End -> JustifyContent.FlexEnd
  MainAxisAlignment.SpaceBetween -> JustifyContent.SpaceBetween
  MainAxisAlignment.SpaceAround -> JustifyContent.SpaceAround
  MainAxisAlignment.SpaceEvenly -> JustifyContent.SpaceEvenly
  else -> throw AssertionError()
}

internal fun JustifyContent.toMainAxisAlignment() = when (this) {
  JustifyContent.FlexStart -> MainAxisAlignment.Start
  JustifyContent.Center -> MainAxisAlignment.Center
  JustifyContent.FlexEnd -> MainAxisAlignment.End
  JustifyContent.SpaceBetween -> MainAxisAlignment.SpaceBetween
  JustifyContent.SpaceAround -> MainAxisAlignment.SpaceAround
  JustifyContent.SpaceEvenly -> MainAxisAlignment.SpaceEvenly
  else -> throw AssertionError()
}

internal fun CrossAxisAlignment.toAlignItems() = when (this) {
  CrossAxisAlignment.Start -> AlignItems.FlexStart
  CrossAxisAlignment.Center -> AlignItems.Center
  CrossAxisAlignment.End -> AlignItems.FlexEnd
  CrossAxisAlignment.Stretch -> AlignItems.Stretch
  else -> throw AssertionError()
}

internal fun AlignItems.toCrossAxisAlignment() = when (this) {
  AlignItems.FlexStart -> CrossAxisAlignment.Start
  AlignItems.Center -> CrossAxisAlignment.Center
  AlignItems.FlexEnd -> CrossAxisAlignment.End
  AlignItems.Stretch -> CrossAxisAlignment.Stretch
  else -> throw AssertionError()
}

internal fun Padding.toSpacing() = Spacing(start, end, top, bottom)

internal fun Spacing.toPadding() = Padding(start, end, top, bottom)
