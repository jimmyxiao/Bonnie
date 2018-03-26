//
//  GridView.swift
//  BonnieDraw
//
//  Created by Professor on 23/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class GridView: UIView {
    @IBInspectable  var lineWidth: CGFloat = 1 {
        didSet {
            setNeedsDisplay()
        }
    }
    @IBInspectable  var horizontalCount: Int = 3
    @IBInspectable  var verticalCount: Int = 3
    @IBInspectable  var gridColor: UIColor = .black {
        didSet {
            setNeedsDisplay()
        }
    }

    override func draw(_ rect: CGRect) {
        if horizontalCount > 0 && verticalCount > 0 {
            let horizontalIncrement = rect.width / CGFloat(horizontalCount)
            let verticalIncrement = rect.height / CGFloat(verticalCount)
            let path = UIBezierPath()
            for xFactor in 1..<horizontalCount {
                let x = CGFloat(xFactor) * horizontalIncrement
                path.move(to: CGPoint(x: x, y: 0))
                path.addLine(to: CGPoint(x: x, y: rect.height))
            }
            for yFactor in 1..<verticalCount {
                let y = CGFloat(yFactor) * verticalIncrement
                path.move(to: CGPoint(x: 0, y: y))
                path.addLine(to: CGPoint(x: rect.width, y: y))
            }
            gridColor.setStroke()
            path.lineWidth = lineWidth
            path.stroke()
        }
    }

    func set(horizontalCount: Int, verticalCount: Int) {
        self.horizontalCount = horizontalCount
        self.verticalCount = verticalCount
        setNeedsDisplay()
    }
}
