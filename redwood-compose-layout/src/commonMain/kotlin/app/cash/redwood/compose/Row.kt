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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import app.cash.redwood.LayoutModifier
import app.cash.redwood.LayoutScopeMarker

@Composable
public fun Row(
  padding: Padding = Padding.Zero,
  overflow: Overflow = Overflow.Clip,
  horizontalAlignment: MainAxisAlignment = MainAxisAlignment.Start,
  verticalAlignment: CrossAxisAlignment = CrossAxisAlignment.Start,
  layoutModifier: LayoutModifier = LayoutModifier,
  children: @Composable RowScope.() -> Unit,
) {
  TODO()
}

@Immutable
@LayoutScopeMarker
public interface RowScope {
  /** Equivalent to `flex-grow`. */
  public fun LayoutModifier.grow(value: Int): LayoutModifier

  /** Equivalent to `flex-shrink`. */
  public fun LayoutModifier.shrink(value: Int): LayoutModifier

  /** Equivalent to `align-self`. */
  public fun LayoutModifier.verticalAlignment(alignment: CrossAxisAlignment): LayoutModifier
}

private object RealRowScope : RowScope {
  override fun LayoutModifier.grow(value: Int) =
    then(GrowLayoutModifier(value))

  override fun LayoutModifier.shrink(value: Int) =
    then(ShrinkLayoutModifier(value))

  override fun LayoutModifier.verticalAlignment(alignment: CrossAxisAlignment) =
    then(VerticalAlignmentLayoutModifier(alignment))
}
