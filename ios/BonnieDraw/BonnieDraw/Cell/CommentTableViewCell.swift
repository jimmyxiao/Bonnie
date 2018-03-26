//
//  CommentTableViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 07/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CommentTableViewCell: CustomSelectorTableViewCell {
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var message: UILabel!
    @IBOutlet weak var date: UILabel!

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
    }
}
