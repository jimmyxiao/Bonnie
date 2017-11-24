//
//  LanguageViewController.swift
//  BonnieDraw
//
//  Created by Professor on 23/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class LanguageViewController: BackButtonViewController {
    override func viewDidLoad() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "back_icon"), style: .plain, target: self, action: #selector(onBackPressed))
    }
}
