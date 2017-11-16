//
//  HueView.swift
//  iOSTemplate
//
//  Created by Professor on 08/11/2017.
//  Copyright © 2017 Agrowood. All rights reserved.
//

import UIKit

class HueView: UIView {
    @IBInspectable  var sliderHeight: CGFloat = 4 {
        didSet {
            setNeedsDisplay()
        }
    }
    @IBInspectable var strokeWidth: CGFloat = 2 {
        didSet {
            setNeedsDisplay()
        }
    }
    @IBInspectable var strokeColor = UIColor.black.withAlphaComponent(0.6) {
        didSet {
            setNeedsDisplay()
        }
    }
    var delegate: HueViewDelegate?
    private var image: UIImage?
    private var touchBounds = CGRect.zero
    private var y: CGFloat = 0, minY: CGFloat = 0, maxY: CGFloat = 0
    private var drawCompletionHandler: (() -> Void)?

    override func draw(_ rect: CGRect) {
        if touchBounds == .zero && bounds.size != .zero {
            minY = (sliderHeight + strokeWidth) / 2
            maxY = bounds.height - minY
            touchBounds = CGRect(x: strokeWidth, y: minY, width: bounds.width - strokeWidth * 2, height: bounds.height - sliderHeight - strokeWidth)
            if let drawCompletionHandler = drawCompletionHandler {
                drawCompletionHandler()
                self.drawCompletionHandler = nil
            }
        }
        if image == nil {
            UIGraphicsBeginImageContextWithOptions(CGSize(width: touchBounds.size.width, height: touchBounds.size.height), false, UIScreen.main.scale)
            if let gradient = CGGradient(colorsSpace: CGColorSpaceCreateDeviceRGB(),
                    colors: [UIColor(hue: 0, saturation: 1, brightness: 1, alpha: 1).cgColor,
                             UIColor(hue: 1 / 6, saturation: 1, brightness: 1, alpha: 1).cgColor,
                             UIColor(hue: 2 / 6, saturation: 1, brightness: 1, alpha: 1).cgColor,
                             UIColor(hue: 3 / 6, saturation: 1, brightness: 1, alpha: 1).cgColor,
                             UIColor(hue: 4 / 6, saturation: 1, brightness: 1, alpha: 1).cgColor,
                             UIColor(hue: 5 / 6, saturation: 1, brightness: 1, alpha: 1).cgColor,
                             UIColor(hue: 1, saturation: 1, brightness: 1, alpha: 1).cgColor] as CFArray,
                    locations: nil) {
                UIGraphicsGetCurrentContext()?.drawLinearGradient(gradient,
                        start: CGPoint.zero,
                        end: CGPoint(x: 0, y: touchBounds.height),
                        options: [])
                if let cgImage = UIGraphicsGetCurrentContext()?.makeImage() {
                    image = UIImage(cgImage: cgImage)
                }
            }
            UIGraphicsEndImageContext()
        }
        image?.draw(in: touchBounds)
        let path = UIBezierPath(rect: CGRect(x: strokeWidth / 2, y: y - sliderHeight / 2, width: touchBounds.width + strokeWidth, height: sliderHeight))
        path.lineWidth = strokeWidth
        strokeColor.setStroke()
        path.stroke()
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        handleTouch(touches, with: event)
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        handleTouch(touches, with: event)
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        handleTouch(touches, with: event)
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        handleTouch(touches, with: event)
    }

    private func handleTouch(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let point = touches.first?.location(in: self) {
            let lastY = y
            if point.y < minY {
                y = minY
            } else if point.y > maxY {
                y = maxY
            } else {
                y = point.y
            }
            if lastY != y {
                delegate?.hue(didSelectHue: (y - minY) / touchBounds.height)
                setNeedsDisplay()
            }
        }
    }

    func set(color: UIColor) {
        if bounds.size != .zero {
            var hue: CGFloat = 0
            color.getHue(&hue, saturation: nil, brightness: nil, alpha: nil)
            if touchBounds == .zero {
                drawCompletionHandler = {
                    self.y = self.touchBounds.size.height * hue + self.minY
                }
            } else {
                y = touchBounds.size.height * hue + minY
                setNeedsDisplay()
            }
        }
    }
}

protocol HueViewDelegate {
    func hue(didSelectHue hue: CGFloat)
}
