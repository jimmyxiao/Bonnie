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
    let position: CGPoint
    let color: (alpha: CGFloat, red: CGFloat, green: CGFloat, blue: CGFloat)
    let action: Action
    let size: CGFloat
    let type: Type
    let duration: TimeInterval

    init(length: UInt16, function: Function, position: CGPoint, color: (alpha: CGFloat, red: CGFloat, green: CGFloat, blue: CGFloat), action: Action, size: CGFloat, type: Type, duration: TimeInterval) {
        self.length = length
        self.function = function
        self.position = position
        self.color = color
        self.action = action
        self.size = size
        self.type = type
        self.duration = duration
    }
}
