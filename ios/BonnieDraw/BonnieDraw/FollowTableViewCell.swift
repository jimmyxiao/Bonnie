//
//  FollowTableViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 25/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class FollowTableViewCell: UITableViewCell {
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var likes: UILabel!

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
