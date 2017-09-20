//
//  SignUpViewController.swift
//  BonnieDraw
//
//  Created by Professor on 20/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SignUpViewController: BackButtonViewController {
    override func viewDidLoad() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "personal_ic_list"), style: .bordered, target: self, action: #selector(onBackPressed))
    }
}
