//
//  CanvasView.swift
//  BonnieDraw
//
//  Created by Professor on 13/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasView: UIImageView {
    var delegate: CanvasViewDelegate? {
        didSet {
            let imageView = UIImageView(frame: frame)
            addAndFill(subView: imageView)
            persistentLayer = imageView
            if let url = delegate?.canvasFileUrl() {
                do {
                    let manager = FileManager.default
                    if manager.fileExists(atPath: url.path) {
                        try manager.removeItem(at: url)
                    }
                    manager.createFile(atPath: url.path, contents: nil, attributes: nil)
                    handle = try FileHandle(forWritingTo: url)
                    self.url = url
                } catch let error {
                    Logger.d(error.localizedDescription)
                }
            }
        }
    }
    var size: CGFloat = 8
    var opacity: CGFloat = 1
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    private var persistentLayer: UIImageView?
    private var lastTime = Date().timeIntervalSince1970
    private var lastPoint = CGPoint.zero
    private var url: URL?
    private var handle: FileHandle?
    private var points = [Point]()
    private var bytes = [UInt8]()
    private var animationLastPoint: Point?
    private var animationCurrentPoint: Point?
    private var animationTimer: Timer?

    func reset() {
        animationTimer?.invalidate()
        points.removeAll()
        image = nil
        persistentLayer?.image = nil
        if let url = delegate?.canvasFileUrl() {
            do {
                let manager = FileManager.default
                if manager.fileExists(atPath: url.path) {
                    try manager.removeItem(at: url)
                }
                manager.createFile(atPath: url.path, contents: nil, attributes: nil)
                handle = try FileHandle(forWritingTo: url)
                self.url = url
            } catch let error {
                Logger.d(error.localizedDescription)
            }
        }
    }

    func play() {
        if let url = url {
            image = nil
            persistentLayer?.image = nil
            do {
                let handler = try FileHandle(forReadingFrom: url)
                parse(data: handler.readDataToEndOfFile())
            } catch let error {
                Logger.d(error.localizedDescription)
            }
        }
    }

    func getImage() -> UIImage? {
        return persistentLayer?.image
    }

    private func parse(data: Data) {
        if !data.isEmpty {
            let scale = (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)
            let byteMax = CGFloat(UInt8.max)
            bytes.append(contentsOf: [UInt8](data))
            while !bytes.isEmpty {
                points.append(Point(
                        length: UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8,
                        function: Function(rawValue: UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) ?? .draw,
                        position: CGPoint(
                                x: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / scale,
                                y: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / scale),
                        color: (CGFloat(bytes.removeFirst()) / byteMax,
                                CGFloat(bytes.removeFirst()) / byteMax,
                                CGFloat(bytes.removeFirst()) / byteMax,
                                CGFloat(bytes.removeFirst()) / byteMax),
                        action: Action(rawValue: bytes.removeFirst()) ?? .move,
                        size: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) * 2 / scale,
                        type: Type(rawValue: bytes.removeFirst()) ?? .round,
                        duration: TimeInterval(Double(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / 1000)))
                bytes.removeFirst()
                bytes.removeFirst()
            }
            animationLastPoint = points.removeFirst()
            let currentPoint = points.removeFirst()
            size = currentPoint.size
            (opacity, red, green, blue) = currentPoint.color
            animationCurrentPoint = currentPoint
            animate()
        }
    }

    private func animate() {
        if let lastPoint = animationLastPoint, let currentPoint = animationCurrentPoint {
            animationTimer = Timer.scheduledTimer(withTimeInterval: currentPoint.duration, repeats: false) {
                timer in
                if currentPoint.action != .down {
                    self.drawLineFrom(fromPoint: lastPoint.position, toPoint: currentPoint.position)
                }
                if !self.points.isEmpty {
                    self.animationLastPoint = currentPoint
                    self.animationCurrentPoint = self.points.removeFirst()
                    self.animate()
                }
            }
        }
    }

    private func savePointsToFile() {
        let scale = (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)
        let byteMax = CGFloat(UInt8.max)
        var bytes = [UInt8]()
        for point in points {
            bytes.append(UInt8(point.length & 0x00ff))
            bytes.append(UInt8(point.length >> 8))
            bytes.append(UInt8(point.function.rawValue & 0x00ff))
            bytes.append(UInt8(point.function.rawValue >> 8))
            let scaledX = UInt16(point.position.x * scale)
            bytes.append(UInt8(scaledX & 0x00ff))
            bytes.append(UInt8(scaledX >> 8))
            let scaledY = UInt16(point.position.y * scale)
            bytes.append(UInt8(scaledY & 0x00ff))
            bytes.append(UInt8(scaledY >> 8))
            bytes.append(UInt8(point.color.alpha * byteMax))
            bytes.append(UInt8(point.color.red * byteMax))
            bytes.append(UInt8(point.color.green * byteMax))
            bytes.append(UInt8(point.color.blue * byteMax))
            bytes.append(point.action.rawValue)
            let scaledSize = UInt16(CGFloat(point.size / 2) * scale)
            bytes.append(UInt8(scaledSize & 0x00ff))
            bytes.append(UInt8(scaledSize >> 8))
            bytes.append(point.type.rawValue)
            let durationMilliseconds = UInt16(Int(point.duration * 1000))
            bytes.append(UInt8(durationMilliseconds & 0x00ff))
            bytes.append(UInt8(durationMilliseconds >> 8))
            bytes.append(0)
            bytes.append(0)
        }
        handle?.write(Data(bytes: bytes))
        points.removeAll()
    }

    private func drawLineFrom(fromPoint: CGPoint, toPoint: CGPoint) {
        UIGraphicsBeginImageContext(bounds.size)
        let context = UIGraphicsGetCurrentContext()
        image?.draw(in: bounds)
        context?.move(to: fromPoint)
        context?.addLine(to: toPoint)
        context?.setLineCap(.round)
        context?.setLineWidth(size)
        context?.setStrokeColor(red: red, green: green, blue: blue, alpha: 1)
        context?.setBlendMode(.normal)
        context?.strokePath()
        image = UIGraphicsGetImageFromCurrentImageContext()
        alpha = alpha
        UIGraphicsEndImageContext()
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first {
            lastTime = Date().timeIntervalSince1970
            lastPoint = touch.location(in: self)
            if bounds.contains(lastPoint) {
                points.append(
                        Point(length: 20,
                                function: .draw,
                                position: lastPoint,
                                color: (opacity, red, green, blue),
                                action: .down,
                                size: size,
                                type: .round,
                                duration: 0))
            }
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first, let coalescedTouches = event?.coalescedTouches(for: touch) {
            var currentPoint = touch.location(in: self)
            var currentTime = Date().timeIntervalSince1970
            for touch in coalescedTouches {
                currentPoint = touch.location(in: self)
                if bounds.contains(currentPoint) {
                    currentTime = Date().timeIntervalSince1970
                    drawLineFrom(fromPoint: lastPoint, toPoint: currentPoint)
                    points.append(
                            Point(length: 20,
                                    function: .draw,
                                    position: currentPoint,
                                    color: (opacity, red, green, blue),
                                    action: .move,
                                    size: size,
                                    type: .round,
                                    duration: currentTime - lastTime))
                    lastPoint = currentPoint
                    lastTime = currentTime
                }
            }
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first, let coalescedTouches = event?.coalescedTouches(for: touch) {
            var currentPoint = touch.location(in: self)
            var currentTime = Date().timeIntervalSince1970
            for touch in coalescedTouches {
                currentPoint = touch.location(in: self)
                if bounds.contains(currentPoint) {
                    currentTime = Date().timeIntervalSince1970
                    drawLineFrom(fromPoint: lastPoint, toPoint: currentPoint)
                    points.append(
                            Point(length: 20,
                                    function: .draw,
                                    position: currentPoint,
                                    color: (opacity, red, green, blue),
                                    action: .move,
                                    size: size,
                                    type: .round,
                                    duration: currentTime - lastTime))
                    lastPoint = currentPoint
                    lastTime = currentTime
                }
            }
        }
        UIGraphicsBeginImageContext(bounds.size)
        persistentLayer?.image?.draw(in: bounds, blendMode: .normal, alpha: 1)
        image?.draw(in: bounds, blendMode: .normal, alpha: alpha)
        persistentLayer?.image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        image = nil
        savePointsToFile()
    }
}

protocol CanvasViewDelegate {
    func canvasFileUrl() -> URL?
}
