//
//  LightStatusBarViewController.swift
//  BonnieDraw
//
//  Created by Professor on 05/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class LightStatusBarViewController: UINavigationController {
    override func viewWillAppear(_ animated: Bool) {
        UIApplication.shared.statusBarStyle = .lightContent
    }

    override func viewWillDisappear(_ animated: Bool) {
        UIApplication.shared.statusBarStyle = .default
    }
}
