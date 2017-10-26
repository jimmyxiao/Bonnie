//
//  FollowTableViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 25/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class FollowTableViewCell: UITableViewCell {
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var status: UILabel!
    @IBOutlet weak var follow: FollowButton!

    override func awakeFromNib() {
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}