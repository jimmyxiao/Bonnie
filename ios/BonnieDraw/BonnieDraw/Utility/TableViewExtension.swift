//
//  TableViewExtension.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 3/30/17.
//  Copyright © 2017 D-Link. All rights reserved.
//

import UIKit

extension UITableView {
    func indexPath(forView view: UIView) -> IndexPath? {
        var view: UIView? = view
        while !(view is UITableViewCell) {
            view = view?.superview
        }
        if let cell = view as? UITableViewCell {
            return indexPath(for: cell)
        }
        return nil
    }
}
