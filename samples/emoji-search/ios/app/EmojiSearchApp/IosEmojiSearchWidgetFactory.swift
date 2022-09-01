//
//  IosEmojiSearchWidgetFactory.swift
//  EmojiSearchApp
//
//  Created by Jesse Wilson on 2022-08-31.
//  Copyright © 2022 Square Inc. All rights reserved.
//

import Foundation

import UIKit
import shared

class IosEmojiSearchWidgetFactory: WidgetEmojiSearchWidgetFactory {
    let imageLoader = RemoteImageLoader()
    
    func Row() -> WidgetRow {
        return RowBinding()
    }
    func Column() -> WidgetColumn {
        return ColumnBinding()
    }
    func ScrollableColumn() -> WidgetScrollableColumn {
        return ScrollableColumnBinding()
    }
    func TextInput() -> WidgetTextInput {
        return TextInputBinding()
    }
    func Text() -> WidgetText {
        return TextBinding()
    }
    func Image() -> WidgetImage {
        return ImageBinding(imageLoader: imageLoader)
    }
}
