//
//  AccountHeaderCollectionReusableView.swift
//  BonnieDraw
//
//  Created by Professor on 17/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountHeaderCollectionReusableView: UICollectionReusableView {
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var editButton: UIButton!
    @IBOutlet weak var worksCount: UILabel!
    @IBOutlet weak var fansCount: UILabel!
    @IBOutlet weak var followsCount: UILabel!

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        editButton.layer.borderColor = UIColor.darkGray.cgColor
    }
}
