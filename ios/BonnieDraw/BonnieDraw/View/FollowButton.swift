//
//  FollowButton.swift
//  BonnieDraw
//
//  Created by Professor on 25/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class FollowButton: UIButton {
    var color: UIColor?
    override var isSelected: Bool {
        didSet {
            if isSelected {
                backgroundColor = nil
                layer.borderWidth = 2
            } else {
                backgroundColor = color
                layer.borderWidth = 0
            }
        }
    }

    override func awakeFromNib() {
        color = backgroundColor
        layer.borderColor = UIColor.lightGray.cgColor
    }
}
