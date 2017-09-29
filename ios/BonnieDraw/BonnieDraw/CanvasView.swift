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
    var lastTimestamp: TimeInterval = -1
    var persistentImage: UIImage?
    private var lastPoint = CGPoint.zero, currentPoint = CGPoint.zero
    private var url: URL?
    private var writeHandle: FileHandle?
    private var readHandle: FileHandle?
    private var animationPaths = [Path]()
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

    func thumbnailData() -> Data? {
        UIGraphicsBeginImageContextWithOptions(bounds.size, false, UIScreen.main.scale)
        persistentImage?.draw(in: bounds)
        for path in paths {
            path.color.setStroke()
            path.bezierPath.stroke()
        }
        var data: Data? = nil
        if let image = UIGraphicsGetImageFromCurrentImageContext() {
            data = UIImagePNGRepresentation(image)
        }
        UIGraphicsEndImageContext()
        return data
    }

    func fileData() -> Data? {
        if let url = url {
            do {
                var data = try Data(contentsOf: url)
                var points = [Point]()
                for path in paths {
                    for point in path.points {
                        points.append(point)
                    }
                }
                data.append(parse(pointsToData: points))
                return data
            } catch let error {
                Logger.d(error.localizedDescription)
            }
        }
        return nil
    }

    override func draw(_ rect: CGRect) {
        persistentImage?.draw(in: rect)
        for path in paths {
            path.color.setStroke()
            path.bezierPath.stroke()
        }
        if !isUserInteractionEnabled {
            if !animationPoints.isEmpty {
                animate()
            } else {
                delegate?.canvasPathsDidFinishAnimation()
                isUserInteractionEnabled = true
                lastTimestamp = -1
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
            writeHandle = try FileHandle(forWritingTo: url)
            self.url = url
        } catch let error {
            Logger.d(error.localizedDescription)
        }
    }

    func reset() {
        animationTimer?.invalidate()
        paths.removeAll()
        redoPaths.removeAll()
        animationPaths.removeAll()
        animationPoints.removeAll()
        persistentImage = nil
        delegate?.canvasPathsDidChange()
        do {
            let manager = FileManager.default
            if let url = url {
                if manager.fileExists(atPath: url.path) {
                    writeHandle?.closeFile()
                    try manager.removeItem(at: url)
                }
                manager.createFile(atPath: url.path, contents: nil, attributes: nil)
                writeHandle = try FileHandle(forWritingTo: url)
            }
        } catch let error {
            Logger.d(error.localizedDescription)
        }
        setNeedsDisplay()
        isUserInteractionEnabled = true
        lastTimestamp = -1
    }

    func play() {
        do {
            isUserInteractionEnabled = false
            animationTimer?.invalidate()
            animationPaths.removeAll()
            animationPoints.removeAll()
            animationPaths.append(contentsOf: paths)
            paths.removeAll()
            if let url = url {
                let readHandle = try FileHandle(forReadingFrom: url)
                let maxByteCount = Int(POINT_BUFFER_COUNT * LENGTH_SIZE)
                let data = readHandle.readData(ofLength: maxByteCount)
                animationPoints.append(contentsOf: parse(dataToPoints: data))
                if data.count < maxByteCount {
                    readHandle.closeFile()
                    for path in animationPaths {
                        for point in path.points {
                            animationPoints.append(point)
                        }
                    }
                } else {
                    self.readHandle = readHandle
                }
            }
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
            delegate?.canvasPathsWillBeginAnimation()
            animate()
            persistentImage = nil
        } catch let error {
            Logger.d(error.localizedDescription)
        }
    }

    private func animate() {
        if bounds.contains(currentPoint) {
            if !animationPoints.isEmpty {
                let point = animationPoints.removeFirst()
                currentPoint = point.position
                if point.action != .down {
                    paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2), controlPoint: lastPoint)
                    paths.last?.points.append(point)
                    if point.action == .up {
                        drawToPersistentImage(saveToFile: false)
                    }
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
                if animationPoints.count < POINT_BUFFER_COUNT / 2, let readHandle = readHandle {
                    let maxByteCount = Int(POINT_BUFFER_COUNT * LENGTH_SIZE)
                    let data = readHandle.readData(ofLength: maxByteCount)
                    animationPoints.append(contentsOf: parse(dataToPoints: data))
                    if data.count < maxByteCount {
                        readHandle.closeFile()
                        self.readHandle = nil
                        for path in animationPaths {
                            for point in path.points {
                                animationPoints.append(point)
                            }
                        }
                    }
                }
            } else {
                isUserInteractionEnabled = true
                lastTimestamp = -1
            }
        }
    }

    private func parse(dataToPoints data: Data) -> [Point] {
        var points = [Point]()
        if !data.isEmpty {
            let scale = (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)
            let byteMax = CGFloat(UInt8.max)
            var bytes = [UInt8](data)
            while !bytes.isEmpty {
                points.append(Point(
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
        return points
    }

    private func parse(pointsToData points: [Point]) -> Data {
        var bytes = [UInt8]()
        if !points.isEmpty {
            let scale = (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)
            let byteMax = CGFloat(UInt8.max)
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
        return Data(bytes: bytes)
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
                let timestamp = touch.timestamp - lastTimestamp
                paths.append(
                        Path(bezierPath: path,
                                points: [Point(length: LENGTH_SIZE,
                                        function: .draw,
                                        position: currentPoint,
                                        color: color,
                                        action: .down,
                                        size: size,
                                        type: .round,
                                        duration: timestamp > MAX_TIMESTAMP ? MAX_TIMESTAMP : timestamp)],
                                color: color))
            }
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let mainTouch = touches.first, let coalescedTouches = event?.coalescedTouches(for: mainTouch) {
            for touch in coalescedTouches {
                currentPoint = touch.location(in: self)
                if bounds.contains(currentPoint) && currentPoint != lastPoint {
                    paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2), controlPoint: lastPoint)
                    let timestamp = touch.timestamp - lastTimestamp
                    paths.last?.points.append(
                            Point(length: LENGTH_SIZE,
                                    function: .draw,
                                    position: currentPoint,
                                    color: color,
                                    action: .move,
                                    size: size,
                                    type: .round,
                                    duration: timestamp > MAX_TIMESTAMP ? MAX_TIMESTAMP : timestamp))
                    lastTimestamp = touch.timestamp
                    lastPoint = currentPoint
                }
            }
            setNeedsDisplay()
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let mainTouch = touches.first, let coalescedTouches = event?.coalescedTouches(for: mainTouch) {
            for touch in coalescedTouches {
                currentPoint = touch.location(in: self)
                let timestamp = touch.timestamp - lastTimestamp
                if bounds.contains(currentPoint) {
                    if currentPoint != lastPoint {
                        paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + lastPoint.x) / 2, y: (currentPoint.y + lastPoint.y) / 2), controlPoint: lastPoint)
                        paths.last?.points.append(
                                Point(length: LENGTH_SIZE,
                                        function: .draw,
                                        position: currentPoint,
                                        color: color,
                                        action: .up,
                                        size: size,
                                        type: .round,
                                        duration: timestamp > MAX_TIMESTAMP ? MAX_TIMESTAMP : timestamp))
                        lastTimestamp = touch.timestamp
                        lastPoint = currentPoint
                    } else {
                        paths.last?.bezierPath.addLine(to: currentPoint)
                        paths.last?.points.append(
                                Point(length: LENGTH_SIZE,
                                        function: .draw,
                                        position: currentPoint,
                                        color: color,
                                        action: .up,
                                        size: size,
                                        type: .round,
                                        duration: timestamp > MAX_TIMESTAMP ? MAX_TIMESTAMP : timestamp))
                    }
                }
            }
            drawToPersistentImage(saveToFile: true)
            setNeedsDisplay()
            redoPaths.removeAll()
            delegate?.canvasPathsDidChange()
        }
    }

    private func drawToPersistentImage(saveToFile save: Bool) {
        if paths.count > PATH_BUFFER_COUNT {
            var pointsToSave = [Point]()
            UIGraphicsBeginImageContextWithOptions(bounds.size, false, UIScreen.main.scale)
            persistentImage?.draw(in: bounds)
            while paths.count > PATH_BUFFER_COUNT {
                let path = paths.removeFirst()
                path.color.setStroke()
                path.bezierPath.stroke()
                for point in path.points {
                    pointsToSave.append(point)
                }
            }
            persistentImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            if save {
                writeHandle?.write(self.parse(pointsToData: pointsToSave))
            }
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
