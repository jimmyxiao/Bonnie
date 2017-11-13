//
//  SaturationBrightnessView.swift
//  iOSTemplate
//
//  Created by Professor on 08/11/2017.
//  Copyright © 2017 Agrowood. All rights reserved.
//

import UIKit

class SaturationBrightnessView: UIView {
    @IBInspectable  var sliderRadius: CGFloat = 2 {
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
    var hue: CGFloat = 0 {
        didSet {
            image = nil
            setNeedsDisplay()
            if touchBounds != .zero {
                delegate?.saturationBrightness(didSelectColor: UIColor(hue: hue, saturation: (point.x - minX) / touchBounds.width, brightness: 1 - (point.y - minY) / touchBounds.height, alpha: 1))
            }
        }
    }
    var delegate: SaturationBrightnessViewDelegate?
    private var image: UIImage?
    private var touchBounds = CGRect.zero
    private var minX: CGFloat = 0, maxX: CGFloat = 0, minY: CGFloat = 0, maxY: CGFloat = 0
    private var point = CGPoint.zero

    override func draw(_ rect: CGRect) {
        if touchBounds == .zero {
            calculateTouchBounds()
        }
        if image == nil {
            UIGraphicsBeginImageContextWithOptions(CGSize(width: touchBounds.size.width, height: touchBounds.size.height), false, UIScreen.main.scale)
            if let gradient = CGGradient(colorsSpace: CGColorSpaceCreateDeviceRGB(),
                    colors: [UIColor(hue: hue, saturation: 1, brightness: 1, alpha: 1).cgColor, UIColor.white.cgColor] as CFArray,
                    locations: nil) {
                UIGraphicsGetCurrentContext()?.drawLinearGradient(gradient,
                        start: CGPoint(x: bounds.width, y: 0),
                        end: .zero,
                        options: [])
            }
            if let gradient = CGGradient(colorsSpace: CGColorSpaceCreateDeviceRGB(),
                    colors: [UIColor.clear.cgColor, UIColor.black.cgColor] as CFArray,
                    locations: nil) {
                UIGraphicsGetCurrentContext()?.drawLinearGradient(gradient,
                        start: CGPoint(x: bounds.width, y: 0),
                        end: CGPoint(x: bounds.width, y: bounds.height),
                        options: [])
            }
            if let cgImage = UIGraphicsGetCurrentContext()?.makeImage() {
                image = UIImage(cgImage: cgImage)
            }
            UIGraphicsEndImageContext()
        }
        image?.draw(in: touchBounds)
        let path = UIBezierPath(arcCenter: point, radius: sliderRadius, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true)
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
            let lastPoint = self.point
            var x = point.x
            var y = point.y
            if x < minX {
                x = minX
            } else if x > maxX {
                x = maxX
            }
            if y < minY {
                y = minY
            } else if y > maxY {
                y = maxY
            }
            let point = CGPoint(x: x, y: y)
            if lastPoint != point {
                self.point = point
                setNeedsDisplay()
                delegate?.saturationBrightness(didSelectColor: UIColor(hue: hue, saturation: (point.x - minX) / touchBounds.width, brightness: 1 - (point.y - minY) / touchBounds.height, alpha: 1))
            }
        }
    }

    func set(color: UIColor) {
        var saturation: CGFloat = 0
        var brightness: CGFloat = 0
        color.getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: nil)
        if touchBounds == .zero {
            calculateTouchBounds()
        }
        point = CGPoint(x: touchBounds.size.width * saturation + minX, y: touchBounds.size.height + minY - (touchBounds.size.height * brightness))
        setNeedsDisplay()
    }

    private func calculateTouchBounds() {
        minX = sliderRadius + strokeWidth / 2
        minY = minX
        maxX = bounds.width - minX
        maxY = bounds.height - minY
        touchBounds = CGRect(x: minX, y: minY, width: bounds.width - minX * 2, height: bounds.height - minY * 2)
    }
}

protocol SaturationBrightnessViewDelegate {
    func saturationBrightness(didSelectColor color: UIColor)
}
