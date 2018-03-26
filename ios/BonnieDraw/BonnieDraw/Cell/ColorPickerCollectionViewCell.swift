//
//  ColorPickerCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 15/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class ColorPickerCollectionViewCell: CustomSelectorCollectionViewCell {
    @IBOutlet weak var removeIcon: UIView!

    override func awakeFromNib() {
        layer.cornerRadius = bounds.width / 2
    }
}
