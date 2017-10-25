//
//  PageViewController.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 1/6/17.
//  Copyright © 2017 D-Link. All rights reserved.
//

import UIKit
import XLPagerTabStrip

class PageViewController: ButtonBarPagerTabStripViewController {
    let titles = ["following".localized, "you".localized]

    override func viewDidLoad() {
        settings.style.selectedBarHeight = 1
        settings.style.buttonBarBackgroundColor = .white
        settings.style.buttonBarItemBackgroundColor = .white
        settings.style.buttonBarItemTitleColor = .black
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationItem.titleView = buttonBarView
    }

    override func viewControllers(for pagerTabStripController: PagerTabStripViewController) -> [UIViewController] {
        var controllers = [UIViewController]()
        for title in titles {
            if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.FOLLOW) as? FollowViewController {
                controller.title = title
                controllers.append(controller)
            }
        }
        return controllers
    }
}
