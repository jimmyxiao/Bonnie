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
    @IBOutlet weak var profileDescription: UILabel!
    @IBOutlet weak var fanButton: UIButton!
    @IBOutlet weak var followButton: UIButton!
    @IBOutlet weak var worksCount: UILabel!
    @IBOutlet weak var fansCount: UILabel!
    @IBOutlet weak var followsCount: UILabel!
    var delegate: AccountHeaderCollectionReusableViewDelegate?

    override func awakeFromNib() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        editButton.layer.borderColor = UIColor.darkGray.cgColor
    }

    @IBAction func headerAction(_ sender: Any) {
        delegate?.accountHeaderAction(sender)
    }

    @IBAction func fans(_ sender: Any) {
        delegate?.accountHeaderFans(sender)
    }

    @IBAction func followings(_ sender: Any) {
        delegate?.accountHeaderFollowings(sender)
    }
}

protocol AccountHeaderCollectionReusableViewDelegate {
    func accountHeaderAction(_ sender: Any)
    func accountHeaderFans(_ sender: Any)
    func accountHeaderFollowings(_ sender: Any)
}
