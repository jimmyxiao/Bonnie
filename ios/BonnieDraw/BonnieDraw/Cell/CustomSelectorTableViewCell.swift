//
//  CustomSelectorTableViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 29/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CustomSelectorTableViewCell: UITableViewCell {
    override func setHighlighted(_ highlighted: Bool, animated: Bool) {
        if highlighted {
            alpha = 0.6
        } else {
            UIView.animate(withDuration: 0.3) {
                self.alpha = 1
            }
        }
    }
}
