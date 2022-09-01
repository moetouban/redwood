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

class ImageBinding: WidgetImage {
    private let root: UIImageView = {
        let view = UIImageView()
        view.contentMode = .scaleAspectFit
        view.setContentHuggingPriority(.required, for: .horizontal)
        return view
    }()
    private let imageLoader: RemoteImageLoader
    private var lastURL: URL?

    init(imageLoader: RemoteImageLoader) {
        self.imageLoader = imageLoader
        self.layoutModifiers = NoopRuntimeLayoutModifier()
    }

    var layoutModifiers: Redwood_runtimeLayoutModifier
    var value: Any { root }

    func url(url: String) {
        root.image = nil

        guard let url = URL(string: url) else {
            return
        }

        lastURL = url
        imageLoader.loadImage(url: url) { [unowned self] url, image in
            guard self.lastURL == url else {
                return
            }

            self.root.image = image
        }
    }
}

class ScaledHeightImageView: UIImageView {

    override var intrinsicContentSize: CGSize {

        if let myImage = self.image {
//            let myImageWidth = myImage.size.width
//            let myImageHeight = myImage.size.height
//            let myViewWidth = self.frame.size.width
//
//            let ratio = myViewWidth/myImageWidth
//            let scaledHeight = myImageHeight * ratio
//
            return CGSize(width: 44, height: 44)
        }
        return CGSize(width: -1.0, height: -1.0)
    }
}

