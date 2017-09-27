//
//  AccountViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountViewController: UIViewController {
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var worksCount: UILabel!
    @IBOutlet weak var fansCount: UILabel!
    @IBOutlet weak var followsCount: UILabel!
    @IBOutlet weak var editButton: UIButton!

    override func viewDidLoad() {
        if let profileImageUrl = UserDefaults.standard.string(forKey: Default.THIRD_PARTY_IMAGE) {
            profileImage.setImage(with: URL(string: profileImageUrl))
        } else {
            //            TODO: Set default profile image
        }
        profileName.text = UserDefaults.standard.string(forKey: Default.THIRD_PARTY_NAME)
        editButton.layer.borderColor = UIColor.darkGray.cgColor
    }
}
