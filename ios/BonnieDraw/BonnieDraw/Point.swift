//
//  Point.swift
//  BonnieDraw
//
//  Created by Professor on 11/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

struct Point {
    let length: UInt16
    let function: Function
    let x: CGFloat
    let y: CGFloat
    let color: (alpha: CGFloat, red: CGFloat, green: CGFloat, blue: CGFloat)
    let action: Action
    let size: CGFloat
    let type: Type

    init(length: UInt16, function: Function, x: CGFloat, y: CGFloat, color: (alpha: CGFloat, red: CGFloat, green: CGFloat, blue: CGFloat), action: Action, size: CGFloat, type: Type) {
        self.length = length
        self.function = function
        self.x = x
        self.y = y
        self.color = color
        self.action = action
        self.size = size
        self.type = type
    }
}
