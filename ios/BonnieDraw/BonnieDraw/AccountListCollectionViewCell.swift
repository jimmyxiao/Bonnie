//
//  AccountListCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 20/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountListCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var likes: UILabel!
    @IBOutlet weak var lastComment: UILabel!
    @IBOutlet weak var secondLastComment: UILabel!
    @IBOutlet weak var lastCommentDate: UILabel!
    override var isHighlighted: Bool {
        didSet {
            alpha = isHighlighted ? 0.5 : 1
        }
    }

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
