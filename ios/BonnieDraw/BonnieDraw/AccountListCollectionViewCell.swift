//
//  AccountListCollectionViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 20/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountListCollectionViewCell: CustomSelectorCollectionViewCell {
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var likes: UILabel!
    @IBOutlet weak var comments: UILabel!
    @IBOutlet weak var openLink: UIButton!
    @IBOutlet weak var collectButton: UIButton!

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
