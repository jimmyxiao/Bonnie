//
//  CollectionViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import XLPagerTabStrip

class CollectionViewController: ButtonBarPagerTabStripViewController {
    override func viewDidLoad() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "back_icon"), style: .plain, target: self, action: #selector(onBackPressed))
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "Add_icon"), style: .plain, target: self, action: #selector(add))
        settings.style.selectedBarBackgroundColor = UIColor.getAccentColor()
        settings.style.selectedBarHeight = 1
        settings.style.buttonBarItemBackgroundColor = .clear
        settings.style.buttonBarItemFont = UIFont.systemFont(ofSize: 15)
        settings.style.buttonBarItemTitleColor = .black
        super.viewDidLoad()
    }

    @objc private func add(_ sender: Any) {
    }

    @objc private func onBackPressed(_ sender: Any) {
        if let navigationController = navigationController {
            if navigationController.popViewController(animated: true) == nil {
                navigationController.dismiss(animated: true)
            }
        } else {
            dismiss(animated: true)
        }
    }

    override func viewControllers(for pagerTabStripController: PagerTabStripViewController) -> [UIViewController] {
        var controllers = [UIViewController]()
        if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.COLLECTION_ALL) {
            controllers.append(controller)
        }
        if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.COLLECTION_SORT) {
            controllers.append(controller)
        }
        return controllers
    }
}
