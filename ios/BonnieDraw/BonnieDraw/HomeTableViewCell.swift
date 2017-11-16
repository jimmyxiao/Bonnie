//
//  HomeTableViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 26/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class HomeTableViewCell: UITableViewCell {
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var likes: UILabel!
    @IBOutlet weak var collectButton: UIButton!
    @IBOutlet weak var lastComment: UILabel!
    @IBOutlet weak var secondLastComment: UILabel!
    @IBOutlet weak var lastCommentDate: UILabel!

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
    }
}
