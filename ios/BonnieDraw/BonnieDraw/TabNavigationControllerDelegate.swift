//
//  TabNavigationControllerDelegate.swift
//  BonnieDraw
//
//  Created by Professor on 08/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class TabNavigationControllerDelegate: NSObject, UINavigationControllerDelegate {
    func navigationController(_ navigationController: UINavigationController, animationControllerFor operation: UINavigationControllerOperation, from fromVC: UIViewController, to toVC: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        if operation == .push && navigationController.viewControllers.count == 1 {
            return TabNavigationControllerAnimator()
        }
        return nil
    }
}
