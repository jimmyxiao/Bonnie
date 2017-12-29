//
//  HomeTableViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 26/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class HomeTableViewCell: CustomSelectorTableViewCell {
    @IBOutlet weak var profileImage: UIButton!
    @IBOutlet weak var profileName: UIButton!
    @IBOutlet weak var followButton: FollowButton!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var likes: UILabel!
    @IBOutlet weak var comments: UILabel!
    @IBOutlet weak var collectButton: UIButton!

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
