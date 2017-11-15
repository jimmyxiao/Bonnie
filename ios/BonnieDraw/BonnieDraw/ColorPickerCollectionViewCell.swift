//
//  ColorPickerCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 15/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class ColorPickerCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var removeIcon: UIView!
    override var isHighlighted: Bool {
        willSet {
            if newValue {
                alpha = 0.5
            }
        }
        didSet {
            if !isHighlighted {
                UIView.animate(withDuration: 0.4) {
                    self.alpha = 1
                }
            }
        }
    }

    override func awakeFromNib() {
        layer.cornerRadius = bounds.width / 2
    }
}
