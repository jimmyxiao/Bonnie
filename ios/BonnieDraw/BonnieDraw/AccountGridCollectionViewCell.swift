//
//  AccountGridCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 17/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountGridCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var thumbnail: UIImageView!
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
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
