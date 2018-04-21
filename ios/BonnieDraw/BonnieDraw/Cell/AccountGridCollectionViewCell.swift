//
//  AccountGridCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 17/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountGridCollectionViewCell: CollectionViewCell {
    @IBOutlet weak var thumbnail: UIImageView!

    override func awakeFromNib() {
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
