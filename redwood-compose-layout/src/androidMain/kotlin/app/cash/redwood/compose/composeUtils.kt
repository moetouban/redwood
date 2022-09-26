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

import androidx.compose.ui.unit.Constraints
import app.cash.redwood.MeasureSpec
import app.cash.redwood.MeasureSpecMode

internal fun Constraints.toMeasureSpecs(): Pair<MeasureSpec, MeasureSpec> {
  val widthSpec = when {
    hasFixedWidth -> MeasureSpec.from(maxWidth, MeasureSpecMode.Exactly)
    hasBoundedWidth -> MeasureSpec.from(maxWidth, MeasureSpecMode.AtMost)
    else -> MeasureSpec.from(minWidth, MeasureSpecMode.Unspecified)
  }
  val heightSpec = when {
    hasFixedHeight -> MeasureSpec.from(maxHeight, MeasureSpecMode.Exactly)
    hasBoundedHeight -> MeasureSpec.from(maxHeight, MeasureSpecMode.AtMost)
    else -> MeasureSpec.from(minHeight, MeasureSpecMode.Unspecified)
  }
  return widthSpec to heightSpec
}
