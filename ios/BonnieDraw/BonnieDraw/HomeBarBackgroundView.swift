//
//  HomeBarBackgroundView.swift
//  BonnieDraw
//
//  Created by Professor on 20/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import DeviceKit

class HomeBarBackgroundView: UIView {
    override func awakeFromNib() {
        let device = Device()
        if device == .iPhoneX || device == .simulator(.iPhoneX) {
            for constraint in constraints {
                if constraint.constant == 44 {
                    constraint.constant = 78
                    return
                }
            }
        }
    }
}
