//
//  CustomTitleNavigationViewController.swift
//  BonnieDraw
//
//  Created by Professor on 20/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CustomTitleNavigationViewController: UINavigationController, UINavigationControllerDelegate {
    var customDelegate: CustomTitleNavigationViewControllerDelegate?
    var showDrawerMenu = false

    override func viewDidLoad() {
        delegate = self
    }

    func navigationController(_ navigationController: UINavigationController, willShow viewController: UIViewController, animated: Bool) {
        if viewController.navigationItem.titleView == nil {
            if showDrawerMenu {
                viewController.navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "menu_ic_out"), style: .plain, target: self, action: #selector(openDrawer))
            }
            let titleView = Bundle.main.loadView(from: "TitleView")
            titleView?.backgroundColor = .clear
            viewController.navigationItem.titleView = titleView
        }
    }

    @objc func openDrawer() {
        customDelegate?.customTitleNavigationOpenDrawer()
    }
}

protocol CustomTitleNavigationViewControllerDelegate {
    func customTitleNavigationOpenDrawer()
}
