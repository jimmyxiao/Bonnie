//
//  CustomTitleNavigationViewController.swift
//  BonnieDraw
//
//  Created by Professor on 20/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CustomTitleNavigationViewController: UINavigationController, UINavigationControllerDelegate {
    let titleView = Bundle.main.loadView(from: "TitleView")

    override func viewDidLoad() {
        navigationBar.items?.first?.titleView = titleView
    }

    func navigationController(_ navigationController: UINavigationController, willShow viewController: UIViewController, animated: Bool) {
        if viewController.navigationItem.titleView == nil {
            viewController.navigationItem.titleView = titleView
        }
    }
}
