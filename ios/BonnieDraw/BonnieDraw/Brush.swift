//
//  Brush.swift
//  iOSTemplate
//
//  Created by Professor on 01/12/2017.
//  Copyright © 2017 Agrowood. All rights reserved.
//
struct Brush {
    static let MIN_VELOCITY: CGFloat = 20
    static let MAX_VELOCITY: CGFloat = 3000
    var defaultMinSize: CGFloat, defaultMaxSize: CGFloat, minSize: CGFloat, maxSize: CGFloat, minAlpha: CGFloat, maxAlpha: CGFloat
    var color = UIColor.black
    var isRotationSupported = false
    var shouldUseVelocity = false
    var velocity: CGFloat = 0
    var lastPoint = CGPoint.zero
    var lastTimestamp = Date()

    init(minSize: CGFloat, maxSize: CGFloat, minAlpha: CGFloat, maxAlpha: CGFloat) {
        defaultMinSize = minSize
        defaultMaxSize = maxSize
        self.minSize = minSize
        self.maxSize = maxSize
        self.minAlpha = minAlpha
        self.maxAlpha = maxAlpha
    }

    private func velocity(forTouch touch: UITouch) -> CGFloat {
        let point = touch.location(in: nil)
        let prevoius = touch.previousLocation(in: nil)
        let velocity = hypot(point.x - prevoius.x, point.y - prevoius.y) / CGFloat(lastTimestamp.timeIntervalSince(Date()))
        var clampedVelocity = velocity
        if clampedVelocity < Brush.MIN_VELOCITY {
            clampedVelocity = Brush.MIN_VELOCITY
        }
        if clampedVelocity > Brush.MAX_VELOCITY {
            clampedVelocity = Brush.MAX_VELOCITY
        }
        return clampedVelocity - Brush.MIN_VELOCITY / Brush.MAX_VELOCITY - Brush.MIN_VELOCITY
    }

    func stepWidthForStroke() -> CGFloat {
        return 2
    }

    func color(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) -> UIColor {
        if shouldUseVelocity {
            var segmentAlpha = (velocity - 1);
            if segmentAlpha > 0 {
                segmentAlpha = 0
            }
            segmentAlpha = minAlpha + abs(segmentAlpha) * (maxAlpha - minAlpha)
            return color.withAlphaComponent(segmentAlpha)
        } else {
            var segmentAlpha = minAlpha + (maxAlpha - minAlpha) * touch.force
            if segmentAlpha < minAlpha {
                segmentAlpha = minAlpha
            }
            return color.withAlphaComponent(segmentAlpha)
        }
    }

    func width(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) -> CGFloat {
        if shouldUseVelocity {
            var width = velocity - 1
            if width > 0 {
                width = 0
            }
            width = minSize + abs(width) * (maxSize - minSize)
            if width < 1 {
                width = 1
            }
            return width
        } else {
            var width = (maxSize + minSize) / 2.0
            width *= coalescedTouch.force
            if width < minSize {
                width = minSize
            }
            if width > maxSize {
                width = maxSize
            }
            return width;
        }
    }

    func texture() -> JotBrushTexture {
        return JotDefaultBrushTexture.sharedInstance()
    }

    func smoothness(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) -> CGFloat {
        return 0.75
    }

    mutating func willBeginStroke(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) {
        velocity = 1
        lastTimestamp = Date()
    }

    mutating func willMoveStroke(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) {
        let duration = lastTimestamp.timeIntervalSince(Date())
        if duration > 0.01 {
            let velocity = self.velocity(forTouch: touch)
            if velocity != 0 {
                self.velocity = velocity
            }
            lastTimestamp = Date()
            lastPoint = touch.location(in: nil)
        }
    }
}
