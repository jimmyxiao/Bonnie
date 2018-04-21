//
//  CustomSelectorCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 29/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CustomSelectorCollectionViewCell: UICollectionViewCell {
    override var isHighlighted: Bool {
        willSet {
            if newValue {
                alpha = 0.6
            }
        }
        didSet {
            if !isHighlighted {
                UIView.animate(withDuration: 0.3) {
                    self.alpha = 1
                }
            }
        }
    }
}
