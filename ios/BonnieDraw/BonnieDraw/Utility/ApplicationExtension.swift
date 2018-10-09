//
//  ApplicationExtension.swift
//  iOSTemplate
//
//  Created by Jason Hsu 08329 on 23/04/2017.
//  Copyright © 2017 Agrowood. All rights reserved.
//

import UIKit

extension UIApplication {
    func replace(rootViewControllerWith viewController: UIViewController) {
        if let window = keyWindow {
            let oldRootViewController = window.rootViewController
            viewController.view.layoutIfNeeded()
            UIView.transition(
                    with: window,
                    duration: 0.4,
                    options: .transitionCrossDissolve,
                    animations: {
                        window.rootViewController = viewController
                    }) {
                finished in
                oldRootViewController?.dismiss(animated: false)
            }
        }
    }

    func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString), canOpenURL(url) {
            open(url)
        }
    }
}
