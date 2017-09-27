//
//  StatusBarBackgroundView.swift
//  Omna
//
//  Created by Professor on 25/09/2017.
//  Copyright © 2017 D-Link. All rights reserved.
//

import UIKit
import DeviceKit

class StatusBarBackgroundView: UIView {
    override func awakeFromNib() {
        let device = Device()
        if device == .iPhoneX || device == .simulator(.iPhoneX) {
            for constraint in constraints {
                if constraint.constant == 20 {
                    constraint.constant = 44
                    return
                }
            }
        }
    }
}
