//
//  ViewExtension.swift
//  Omna
//
//  Created by Professor on 19/05/2017.
//  Copyright © 2017 D-Link. All rights reserved.
//

import UIKit

extension UIView {
    func addAndCenter(subView: UIView) {
        subView.translatesAutoresizingMaskIntoConstraints = false
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(subView)
        addConstraints([NSLayoutConstraint(item: subView, attribute: .centerX, relatedBy: .equal, toItem: self, attribute: .centerX, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: subView, attribute: .centerY, relatedBy: .equal, toItem: self, attribute: .centerY, multiplier: 1, constant: 0)])
    }

    func addAndFill(subView: UIView) {
        subView.translatesAutoresizingMaskIntoConstraints = false
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(subView)
        addConstraints([NSLayoutConstraint(item: subView, attribute: .leading, relatedBy: .equal, toItem: self, attribute: .leading, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: subView, attribute: .trailing, relatedBy: .equal, toItem: self, attribute: .trailing, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: subView, attribute: .top, relatedBy: .equal, toItem: self, attribute: .top, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: subView, attribute: .bottom, relatedBy: .equal, toItem: self, attribute: .bottom, multiplier: 1, constant: 0)])
    }

    func addLeadingBorder(withColor color: UIColor, width: CGFloat) {
        let layer = CALayer()
        layer.backgroundColor = color.cgColor
        layer.frame = CGRect(x: 0, y: 0, width: width, height: bounds.height)
        self.layer.addSublayer(layer)
    }

    func addTrailingBorder(withColor color: UIColor, width: CGFloat) {
        let layer = CALayer()
        layer.backgroundColor = color.cgColor
        layer.frame = CGRect(x: bounds.width - width, y: 0, width: width, height: bounds.height)
        self.layer.addSublayer(layer)
    }

    func addTopBorder(withColor color: UIColor, width: CGFloat) {
        let layer = CALayer()
        layer.backgroundColor = color.cgColor
        layer.frame = CGRect(x: 0, y: 0, width: bounds.width, height: width)
        self.layer.addSublayer(layer)
    }

    func addBottomBorder(withColor color: UIColor, width: CGFloat) {
        let layer = CALayer()
        layer.backgroundColor = color.cgColor
        layer.frame = CGRect(x: 0, y: bounds.height - width, width: bounds.width, height: width)
        self.layer.addSublayer(layer)
    }
}
