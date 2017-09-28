//
//  CanvasView.swift
//  BonnieDraw
//
//  Created by Professor on 13/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasView: UIView {
    var delegate: CanvasViewDelegate?
    var size: CGFloat = 8
    var color = UIColor.black
    var paths = [Path]()
    var redoPaths = [Path]()
    private var lastTimestamp: TimeInterval = -1
    private var lastPoint = CGPoint.zero
    private var currentPoint = CGPoint.zero
    private var url: URL?
    private var animationPoints = [Point]()
    private var animationTimer: Timer?

    func undo() {
        if !paths.isEmpty {
            redoPaths.append(paths.removeLast())
            delegate?.canvasPathsDidChange()
            setNeedsDisplay()
        }
    }

    func redo() {
        if !redoPaths.isEmpty {
            paths.append(redoPaths.removeLast())
            delegate?.canvasPathsDidChange()
            setNeedsDisplay()
        }
    }

    override func draw(_ rect: CGRect) {
        for path in paths {
            path.color.setStroke()
            path.bezierPath.stroke()
        }
        if !isUserInteractionEnabled {
            if !animationPoints.isEmpty {
                animate()
            } else {
                isUserInteractionEnabled = true
                lastTimestamp = -1
                delegate?.canvasPathsDidFinishAnimation()
            }
        }
    }

    override func awakeFromNib() {
        do {
            let manager = FileManager.default
            let url = try FileManager.default.url(for: .documentationDirectory, in: .userDomainMask, appropriateFor: nil, create: true).appendingPathComponent("temp.bdw")
            if manager.fileExists(atPath: url.path) {
                try manager.removeItem(at: url)
            }
            manager.createFile(atPath: url.path, contents: nil, attributes: nil)
            self.url = url
        } catch let error {
            Logger.d(error.localizedDescription)
        }
    }

    func reset() {
        animationTimer?.invalidate()
        paths.removeAll()
        delegate?.canvasPathsDidChange()
        animationPoints.removeAll()
        do {
            let manager = FileManager.default
            let url = try FileManager.default.url(for: .documentationDirectory, in: .userDomainMask, appropriateFor: nil, create: true).appendingPathComponent("temp.bdw")
            if manager.fileExists(atPath: url.path) {
                try manager.removeItem(at: url)
            }
            manager.createFile(atPath: url.path, contents: nil, attributes: nil)
            self.url = url
        } catch let error {
            Logger.d(error.localizedDescription)
        }
        setNeedsDisplay()
        isUserInteractionEnabled = true
        lastTimestamp = -1
    }

    func play() {
        isUserInteractionEnabled = false
        savePointsToFile()
        animationTimer?.invalidate()
        animationPoints.removeAll()
        for path in paths {
            for point in path.points {
                animationPoints.append(point)
            }
        }
        paths.removeAll()
        let point = animationPoints.removeFirst()
        currentPoint = point.position
        lastPoint = currentPoint
        if bounds.contains(currentPoint) {
            color = point.color
            size = point.size
            let path = UIBezierPath()
            path.move(to: currentPoint)
            path.lineCapStyle = .round
            path.lineWidth = size
            paths.append(
                    Path(bezierPath: path,
                            points: [point],
                            color: color))
        }
        animate()
    }

    private func parse(data: Data) {
        if !data.isEmpty {
            delegate?.canvasPathsWillBeginAnimation()
            let scale = (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)
            let byteMax = CGFloat(UInt8.max)
            var bytes = [UInt8](data)
            while !bytes.isEmpty {
                animationPoints.append(Point(
                        length: UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8,
                        function: Function(rawValue: UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) ?? .draw,
                        position: CGPoint(
                                x: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / scale,
                                y: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / scale),
                        color: UIColor(
                                red: CGFloat(bytes.removeFirst()) / byteMax,
                                green: CGFloat(bytes.removeFirst()) / byteMax,
                                blue: CGFloat(bytes.removeFirst()) / byteMax,
                                alpha: CGFloat(bytes.removeFirst()) / byteMax),
                        action: Action(rawValue: bytes.removeFirst()) ?? .move,
                        size: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) * 2 / scale,
                        type: Type(rawValue: bytes.removeFirst()) ?? .round,
                        duration: TimeInterval(Double(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / 1000)))
                bytes.removeFirst()
                bytes.removeFirst()
            }
        }
    }

    private func animate() {
        if bounds.contains(currentPoint) {
            if !animationPoints.isEmpty {
                let point = animationPoints.removeFirst()
                currentPoint = point.position
                if point.action != .down {
                    let middle = CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2)
                    paths.last?.bezierPath.addQuadCurve(to: middle, controlPoint: lastPoint)
                    paths.last?.points.append(point)
                    animationTimer = Timer.scheduledTimer(withTimeInterval: point.duration, repeats: false) {
                        timer in
                        self.setNeedsDisplay()
                    }
                    lastPoint = currentPoint
                } else {
                    currentPoint = point.position
                    lastPoint = currentPoint
                    if bounds.contains(currentPoint) {
                        color = point.color
                        size = point.size
                        let path = UIBezierPath()
                        path.move(to: currentPoint)
                        path.lineCapStyle = .round
                        path.lineWidth = size
                        paths.append(
                                Path(bezierPath: path,
                                        points: [point],
                                        color: color))
                    }
                    animate()
                }
            } else {
                isUserInteractionEnabled = true
                lastTimestamp = -1
            }
        }
    }

    private func savePointsToFile() {
        if let url = url {
            let handle = FileHandle(forWritingAtPath: url.path)
            let scale = (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)
            let byteMax = CGFloat(UInt8.max)
            var bytes = [UInt8]()
            for path in paths {
                for point in path.points {
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
                    let ciColor = CIColor(color: point.color)
                    bytes.append(UInt8(ciColor.red * byteMax))
                    bytes.append(UInt8(ciColor.green * byteMax))
                    bytes.append(UInt8(ciColor.blue * byteMax))
                    bytes.append(UInt8(ciColor.alpha * byteMax))
                    bytes.append(point.action.rawValue)
                    let scaledSize = UInt16(CGFloat(point.size / 2) * scale)
                    bytes.append(UInt8(scaledSize & 0x00ff))
                    bytes.append(UInt8(scaledSize >> 8))
                    bytes.append(point.type.rawValue)
                    let durationMilliseconds = point.duration * 1000 < Double(UInt16.max) ? UInt16(Int(point.duration * 1000)) : UInt16.max
                    bytes.append(UInt8(durationMilliseconds & 0x00ff))
                    bytes.append(UInt8(durationMilliseconds >> 8))
                    bytes.append(0)
                    bytes.append(0)
                }
            }
            handle?.write(Data(bytes: bytes))
        }
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first {
            if lastTimestamp < 0 {
                lastTimestamp = touch.timestamp
            }
            currentPoint = touch.location(in: self)
            lastPoint = currentPoint
            if bounds.contains(currentPoint) {
                let path = UIBezierPath()
                path.move(to: currentPoint)
                path.lineCapStyle = .round
                path.lineWidth = size
                paths.append(
                        Path(bezierPath: path,
                                points: [Point(length: LENGTH_SIZE,
                                        function: .draw,
                                        position: currentPoint,
                                        color: color,
                                        action: .down,
                                        size: size,
                                        type: .round,
                                        duration: touch.timestamp - lastTimestamp)],
                                color: color))
            }
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let mainTouch = touches.first, let coalescedTouches = event?.coalescedTouches(for: mainTouch) {
            var middle: CGPoint
            var lastPoint = self.lastPoint
            for touch in coalescedTouches {
                currentPoint = touch.location(in: self)
                if bounds.contains(currentPoint) {
                    middle = CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2)
                    paths.last?.bezierPath.addQuadCurve(to: middle, controlPoint: lastPoint)
                    paths.last?.points.append(
                            Point(length: LENGTH_SIZE,
                                    function: .draw,
                                    position: currentPoint,
                                    color: color,
                                    action: .move,
                                    size: size,
                                    type: .round,
                                    duration: touch.timestamp - lastTimestamp))
                    lastTimestamp = touch.timestamp
                }
                lastPoint = currentPoint
            }
            setNeedsDisplay()
            self.lastPoint = currentPoint
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let mainTouch = touches.first, let coalescedTouches = event?.coalescedTouches(for: mainTouch) {
            var middle: CGPoint
            var lastPoint = self.lastPoint
            for touch in coalescedTouches {
                currentPoint = touch.location(in: self)
                if bounds.contains(currentPoint) {
                    middle = CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2)
                    paths.last?.bezierPath.addQuadCurve(to: middle, controlPoint: lastPoint)
                    paths.last?.points.append(
                            Point(length: LENGTH_SIZE,
                                    function: .draw,
                                    position: currentPoint,
                                    color: color,
                                    action: .move,
                                    size: size,
                                    type: .round,
                                    duration: touch.timestamp - lastTimestamp))
                    lastTimestamp = touch.timestamp
                }
                lastPoint = currentPoint
            }
            setNeedsDisplay()
            redoPaths.removeAll()
            delegate?.canvasPathsDidChange()
            self.lastPoint = currentPoint
        }
    }

    private func calculateRectForRedraw() -> CGRect {
        let p = CGPoint(x: min(currentPoint.x, lastPoint.x) - size, y: min(currentPoint.y, lastPoint.y - size))
        let s = CGSize(width: max(currentPoint.x, lastPoint.x) - p.x + size, height: max(currentPoint.y, lastPoint.y) - p.y + size)
        return CGRect(origin: p, size: s)
    }
}

protocol CanvasViewDelegate {
    func canvasPathsDidChange()

    func canvasPathsWillBeginAnimation()

    func canvasPathsDidFinishAnimation()
}
