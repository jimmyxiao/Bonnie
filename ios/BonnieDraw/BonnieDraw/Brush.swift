//
//  Brush.swift
//  iOSTemplate
//
//  Created by Professor on 01/12/2017.
//  Copyright © 2017 Agrowood. All rights reserved.
//
class Brush: JotBrushTexture {
    static let MIN_VELOCITY: CGFloat = 20
    static let MAX_VELOCITY: CGFloat = 3000
    private var velocity: CGFloat = 0
    private var lastPoint = CGPoint.zero
    private var lastTimestamp = Date()
    private var imageName: String?
    var image: UIImage?
    var minSize: CGFloat, maxSize: CGFloat, minAlpha: CGFloat, maxAlpha: CGFloat
    var color = UIColor.black
    var isRotationSupported = false, isForceSupported = false, isVelocitySupported = false
    var stepWidth: CGFloat = 1
    var smoothness: CGFloat = 0.5
    var type: Type {
        didSet {
            switch type {
            case .crayon:
                imageName = "Crayon"
            case .pencil:
                imageName = "Pencil"
            case .pen:
                imageName = nil
            case .airbrush:
                imageName = "Airbrush"
            case .marker:
                imageName = "Marker"
            default:
                return
            }
            if image != nil {
                image = nil
            }
        }
    }

    init(withBrushType type: Type, minSize: CGFloat, maxSize: CGFloat, minAlpha: CGFloat, maxAlpha: CGFloat) {
        self.type = type
        self.minSize = minSize
        self.maxSize = maxSize
        self.minAlpha = minAlpha
        self.maxAlpha = maxAlpha
        super.init()
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
        return stepWidth
    }

    func color(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) -> UIColor? {
        if type != .eraser {
            if isVelocitySupported {
                return color.withAlphaComponent(minAlpha + (1 - velocity) * (maxAlpha - minAlpha))
            } else if isForceSupported {
                return color.withAlphaComponent(minAlpha + (maxAlpha - minAlpha) * touch.force)
            } else {
                return color.withAlphaComponent(maxAlpha)
            }
        } else {
            return nil
        }
    }

    func width(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) -> CGFloat {
        if type != .eraser {
            if isVelocitySupported {
                return minSize + (1 - velocity) * (maxSize - minSize)
            } else if isForceSupported {
                var width = (maxSize + minSize) / 2
                width *= coalescedTouch.force
                if width < minSize {
                    width = minSize
                }
                if width > maxSize {
                    width = maxSize
                }
                return width
            } else {
                return minSize
            }
        } else {
            if isVelocitySupported {
                return maxSize + (1 - velocity) * (minSize - maxSize)
            } else if isForceSupported {
                let width = minSize + (maxSize - minSize) * coalescedTouch.force
                return max(minSize, min(maxSize, width))
            } else {
                return minSize
            }
        }
    }

    func texture() -> JotBrushTexture {
        return brushTexture()
    }

    func smoothness(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) -> CGFloat {
        return smoothness
    }

    func willBeginStroke(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) {
        if isVelocitySupported {
            velocity = 1
            lastTimestamp = Date()
        }
    }

    func willMoveStroke(forCoalescedTouch coalescedTouch: UITouch, fromTouch touch: UITouch) {
        if isVelocitySupported {
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

    private func brushTexture() -> JotBrushTexture {
        if type != .eraser {
            if let context = JotGLContext.current() as? JotGLContext {
                if let image = image {
                    if let texture = JotSharedBrushTexture(image: image) {
                        context.contextProperties[""] = texture
                        return texture
                    } else {
                        return JotDefaultBrushTexture.sharedInstance()
                    }
                } else {
                    if let texture = context.contextProperties[imageName] as? JotSharedBrushTexture {
                        return texture
                    } else if let imageName = imageName, let texture = JotSharedBrushTexture(image: UIImage(named: imageName)) {
                        context.contextProperties[imageName] = texture
                        return texture
                    } else {
                        return JotDefaultBrushTexture.sharedInstance()
                    }
                }
            }
            return self
        } else {
            return JotDefaultBrushTexture.sharedInstance()
        }
    }

    override func bind() -> Bool {
        return brushTexture().bind()
    }

    override func unbind() {
        return brushTexture().unbind()
    }

    required init!(from dictionary: [AnyHashable: Any]!) {
        fatalError("init(from:) has not been implemented")
    }
}
