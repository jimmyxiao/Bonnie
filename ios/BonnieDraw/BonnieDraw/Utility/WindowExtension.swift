//
//  WindowExtension.swift
//  BonnieDraw
//
//  Created by Professor on 30/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

extension UIWindow {
    func topController() -> UIViewController? {
        var topController = rootViewController
        while let controller = topController?.presentedViewController {
            topController = controller
        }
        return topController
    }
}
