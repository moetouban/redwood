//
//  ScrollableColumnBinding.swift
//  EmojiSearchApp
//
//  Created by Kyle Bashour on 8/30/22.
//  Copyright © 2022 Square Inc. All rights reserved.
//

import UIKit
import shared

class ScrollableColumnBinding: NSObject, WidgetScrollableColumn {
    private let root = UITableView()
    private var views: [UIView] = []

    override init() {
        super.init()
        root.dataSource = self
    }

    lazy var children: Redwood_widgetWidgetChildren = ChildrenBinding { [unowned self] views in
        self.views = views
        root.reloadData()
    }

    var layoutModifiers: Redwood_runtimeLayoutModifier = NoopRuntimeLayoutModifier()
    var value: Any { root }
}

extension ScrollableColumnBinding: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        views.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = TableViewCell(view: views[indexPath.row])
        /// This probably won't work correctly
//        cell.contentView.addSubview(views[indexPath.row])
        return cell
    }
}

private class TableViewCell: UITableViewCell {
    private let view: UIView
    
    init(view: UIView) {
        self.view = view
        super.init(style: .default, reuseIdentifier: nil)
        
        addSubview(view)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        
        view.frame = bounds
    }
}
