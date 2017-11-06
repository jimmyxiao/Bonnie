//
//  ShadowView.swift
//  TainanEvent
//
//  Created by Professor on 13/06/2017.
//  Copyright © 2017 Agrowood. All rights reserved.
//

import UIKit

//@IBDesignable
class ShadowView: UIView {
    @IBInspectable var offSetX: CGFloat = 0 {
        didSet {
            setNeedsDisplay()
        }
    }
    @IBInspectable var offSetY: CGFloat = 2 {
        didSet {
            setNeedsDisplay()
        }
    }

    override func awakeFromNib() {
        layer.shadowOffset = CGSize(width: offSetX, height: offSetY)
    }
}
