//
//  CollectionViewExtension.swift
//  BonnieDraw
//
//  Created by Professor on 22/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

extension UICollectionView {
    func indexPath(forView view: UIView) -> IndexPath? {
        var view: UIView? = view
        while !(view is UICollectionViewCell) {
            view = view?.superview
        }
        if let cell = view as? UICollectionViewCell {
            return indexPath(for: cell)
        }
        return nil
    }
}
