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

import app.cash.redwood.widget.Widget

public interface RowWidget<T : Any> : Widget<T> {
  public var padding: Padding
  public var overflow: Overflow
  public var horizontalAlignment: MainAxisAlignment
  public var verticalAlignment: CrossAxisAlignment
}

public interface ColumnWidget<T : Any> : Widget<T> {
  public var padding: Padding
  public var overflow: Overflow
  public var horizontalAlignment: CrossAxisAlignment
  public var verticalAlignment: MainAxisAlignment
}
