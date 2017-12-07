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
        settings.style.selectedBarBackgroundColor = UIColor.getAccentColor()
        settings.style.selectedBarHeight = 1
        settings.style.buttonBarItemBackgroundColor = .clear
        settings.style.buttonBarItemFont = UIFont.systemFont(ofSize: 15)
        settings.style.buttonBarItemTitleColor = .black
        super.viewDidLoad()
    }

    @IBAction func onBackPressed(_ sender: Any) {
        if let navigationController = navigationController {
            if navigationController.popViewController(animated: true) == nil {
                navigationController.dismiss(animated: true)
            }
        } else {
            dismiss(animated: true)
        }
    }

    @IBAction func add(_ sender: Any) {
        performSegue(withIdentifier: Segue.COLLECTION, sender: nil)
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
