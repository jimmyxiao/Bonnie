//
//  CanvasView.swift
//  BonnieDraw
//
//  Created by Professor on 13/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasView: UIView {
    var size: CGFloat = 8
    var color = UIColor.black
    private var lastTime = Date().timeIntervalSince1970
    private var lastPoint = CGPoint.zero
    private var currentPoint = CGPoint.zero
    private var url: URL?
    private var paths = [Path]()
    private var animationPoints = [Point]()
    private var animationTimer: Timer?

    override func draw(_ rect: CGRect) {
        for path in paths {
            path.color.setStroke()
            path.bezierPath.stroke()
        }
        if !animationPoints.isEmpty {
            animate()
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
    }

    func play() {
        savePointsToFile()
        animationTimer?.invalidate()
        paths.removeAll()
        animationPoints.removeAll()
        if let url = url {
            do {
                let handle = try FileHandle(forReadingFrom: url)
                parse(data: handle.readDataToEndOfFile())
            } catch let error {
                Logger.d(error.localizedDescription)
            }
        }
    }

    private func parse(data: Data) {
        if !data.isEmpty {
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
            let point = animationPoints.removeFirst()
            currentPoint = point.position
            lastPoint = currentPoint
            if bounds.contains(currentPoint) {
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
    }

    private func animate() {
        var middle: CGPoint
        if bounds.contains(currentPoint) {
            lastPoint = currentPoint
            if !animationPoints.isEmpty {
                let point = animationPoints.removeFirst()
                currentPoint = point.position
                if point.action != .down {
                    middle = CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2)
                    paths.last?.bezierPath.addQuadCurve(to: middle, controlPoint: lastPoint)
                    paths.last?.points.append(point)
                    animationTimer = Timer.scheduledTimer(withTimeInterval: point.duration, repeats: false) {
                        timer in
                        self.setNeedsDisplay()
                    }
                } else {
                    lastPoint = currentPoint
                    if bounds.contains(currentPoint) {
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
                    let durationMilliseconds = UInt16(Int(point.duration * 1000))
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
            lastTime = Date().timeIntervalSince1970
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
                                        duration: 0)],
                                color: color))
            }
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first, let coalescedTouches = event?.coalescedTouches(for: touch) {
            var currentTime = Date().timeIntervalSince1970
            var middle: CGPoint
            for touch in coalescedTouches {
                lastPoint = currentPoint
                currentPoint = touch.location(in: self)
                if bounds.contains(currentPoint) {
                    middle = CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2)
                    currentTime = Date().timeIntervalSince1970
                    paths.last?.bezierPath.addQuadCurve(to: middle, controlPoint: lastPoint)
                    paths.last?.points.append(
                            Point(length: LENGTH_SIZE,
                                    function: .draw,
                                    position: currentPoint,
                                    color: color,
                                    action: .move,
                                    size: size,
                                    type: .round,
                                    duration: currentTime - lastTime))
                    lastTime = currentTime
                }
            }
            setNeedsDisplay()
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first, let coalescedTouches = event?.coalescedTouches(for: touch) {
            var currentTime = Date().timeIntervalSince1970
            var middle: CGPoint
            for touch in coalescedTouches {
                lastPoint = currentPoint
                currentPoint = touch.location(in: self)
                if bounds.contains(currentPoint) {
                    middle = CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2)
                    currentTime = Date().timeIntervalSince1970
                    paths.last?.bezierPath.addQuadCurve(to: middle, controlPoint: lastPoint)
                    paths.last?.points.append(
                            Point(length: LENGTH_SIZE,
                                    function: .draw,
                                    position: currentPoint,
                                    color: color,
                                    action: .move,
                                    size: size,
                                    type: .round,
                                    duration: currentTime - lastTime))
                    lastTime = currentTime
                }
            }
            setNeedsDisplay()
        }
    }
}
