//
//  Path.swift
//  BonnieDraw
//
//  Created by Professor on 15/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class Path: NSObject {
    let blendMode: CGBlendMode
    let bezierPath: UIBezierPath
    var points: [Point]
    let color: UIColor

    init(blendMode: CGBlendMode, bezierPath: UIBezierPath, points: [Point], color: UIColor) {
        self.blendMode = blendMode
        self.bezierPath = bezierPath
        self.points = points
        self.color = color
    }
}
