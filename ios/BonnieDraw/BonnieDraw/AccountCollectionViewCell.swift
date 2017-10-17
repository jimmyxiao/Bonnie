//
//  AccountCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 17/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var thumbnail: UIImageView!
    override var isHighlighted: Bool {
        didSet {
            alpha = isHighlighted ? 0.5 : 1
        }
    }

    override func awakeFromNib() {
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
