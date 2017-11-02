//
//  SizePickerTableViewCell.swift
//  BonnieDraw
//
//  Created by Professor on 28/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SizePickerTableViewCell: UITableViewCell {
    var radius: CGFloat = 0 {
        didSet {
            setNeedsDisplay()
        }
    }

    override func draw(_ rect: CGRect) {
        UIBezierPath(arcCenter: CGPoint(x: rect.width / 2, y: rect.height / 2), radius: radius, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).fill()
    }
}
