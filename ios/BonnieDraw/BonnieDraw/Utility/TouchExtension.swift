//
//  TouchExtension.swift
//  BonnieDraw
//
//  Created by Professor on 03/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

extension UITouch {
    func getAction() -> Action? {
        switch phase {
        case .began:
            return .down
        case .moved:
            return .move
        case .ended:
            return .up
        default:
            return nil
        }
    }
}
