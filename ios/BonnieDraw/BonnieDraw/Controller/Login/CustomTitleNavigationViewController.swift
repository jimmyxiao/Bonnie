//
//  CustomTitleNavigationViewController.swift
//  BonnieDraw
//
//  Created by Professor on 20/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CustomTitleNavigationViewController: UINavigationController, UINavigationControllerDelegate {
    override func viewDidLoad() {
        delegate = self
    }

    func navigationController(_ navigationController: UINavigationController, willShow viewController: UIViewController, animated: Bool) {
        if viewController.navigationItem.titleView == nil {
            viewController.navigationItem.titleView = UIImageView(image: UIImage(named: "title_logo"))
        }
    }
}
