//
//  TabBarViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class TabBarViewController: UIViewController, CustomTitleNavigationViewControllerDelegate {
    var delegate: TabBarViewControllerDelegate?

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? CustomTitleNavigationViewController {
            controller.customDelegate = self
            controller.showDrawerMenu = true
        }
    }

    func customTitleNavigationOpenDrawer() {
        delegate?.tabBarOpenDrawer()
    }
}

protocol TabBarViewControllerDelegate {
    func tabBarOpenDrawer()
}
