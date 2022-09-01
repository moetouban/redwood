//
//  ImageBinding.swift
//  EmojiSearchApp
//
//  Created by Kyle Bashour on 8/30/22.
//  Copyright © 2022 Square Inc. All rights reserved.
//

import Foundation
import shared
import UIKit

class TextBinding: WidgetText {
    private let root: UILabel = {
        let view = UILabel()
        return view
    }()

    init() {
        self.layoutModifiers = NoopRuntimeLayoutModifier()
    }

    var layoutModifiers: Redwood_runtimeLayoutModifier
    var value: Any { root }

    func text(text: String) {
        root.text = text
    }
}
