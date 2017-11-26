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
    var type = Type.pen
    var paths = [Path]()
    var redoPaths = [Path]()
    var persistentBackgroundColor: UIColor?
    var persistentImage: UIImage?
    private var lastTimestamp: TimeInterval = -1
    private var url = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("cache.bdw")
    private var writeHandle: FileHandle?

    override func awakeFromNib() {
        do {
            let manager = FileManager.default
            if manager.fileExists(atPath: url.path) {
                try manager.removeItem(at: url)
            }
            manager.createFile(atPath: url.path, contents: nil, attributes: nil)
            writeHandle = try FileHandle(forWritingTo: url)
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
        }
    }

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
        let context = UIGraphicsGetCurrentContext()
        var backgroundColor = persistentBackgroundColor
        for path in paths {
            if path.points.first?.type == .background {
                backgroundColor = path.color
            }
        }
        if let backgroundColor = backgroundColor {
            backgroundColor.setFill()
            context?.fill(bounds)
        }
        persistentImage?.draw(in: bounds)
        for path in paths {
            context?.setBlendMode(path.blendMode)
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

    override func draw(_ rect: CGRect) {
        persistentImage?.draw(in: bounds)
        let context = UIGraphicsGetCurrentContext()
        var backgroundColor: UIColor? = nil
        for path in paths {
            if path.points.first?.type != .background {
                context?.setBlendMode(path.blendMode)
                path.color.setStroke()
                path.bezierPath.stroke()
            } else {
                backgroundColor = path.color
            }
        }
        delegate?.canvas(changeBackgroundColor: backgroundColor ?? persistentBackgroundColor ?? .white)
        if paths.last?.blendMode == .clear,
           let point = paths.last?.points.last,
           point.action == .move {
            context?.setBlendMode(.normal)
            UIColor.black.setStroke()
            UIBezierPath(arcCenter: point.position, radius: point.size / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        }
    }

    func reset() {
        writeHandle?.closeFile()
        paths.removeAll()
        redoPaths.removeAll()
        persistentBackgroundColor = nil
        persistentImage = nil
        delegate?.canvasPathsDidChange()
        do {
            let manager = FileManager.default
            if manager.fileExists(atPath: url.path) {
                try manager.removeItem(at: url)
            }
            manager.createFile(atPath: url.path, contents: nil, attributes: nil)
            writeHandle = try FileHandle(forWritingTo: url)
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
        }
        setNeedsDisplay()
        lastTimestamp = -1
    }

    func close() {
        writeHandle?.closeFile()
    }

    func save(toUrl url: URL? = nil, completionHandler: ((URL?) -> Void)? = nil) {
        let bounds = self.bounds
        DispatchQueue.global().async {
            do {
                let manager = FileManager.default
                let url = try url ?? manager.url(
                        for: .documentationDirectory,
                        in: .userDomainMask,
                        appropriateFor: nil,
                        create: true).appendingPathComponent("draft.bdw")
                if manager.fileExists(atPath: url.path) {
                    try manager.removeItem(at: url)
                }
                try manager.copyItem(at: self.url, to: url)
                let writeHandle = try FileHandle(forWritingTo: url)
                writeHandle.seekToEndOfFile()
                var points = [Point]()
                for path in self.paths {
                    points.append(contentsOf: path.points)
                }
                writeHandle.write(DataConverter.parse(pointsToData: points, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
                writeHandle.closeFile()
                DispatchQueue.main.async {
                    completionHandler?(url)
                }
            } catch {
                Logger.d("\(#function): \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completionHandler?(nil)
                }
            }
        }
    }

    func load(fromUrl url: URL? = nil, completionHandler: @escaping () -> Void) {
        let bounds = self.bounds
        DispatchQueue.global().async {
            do {
                let manager = FileManager.default
                let url = try url ?? manager.url(
                        for: .documentationDirectory,
                        in: .userDomainMask,
                        appropriateFor: nil,
                        create: true).appendingPathComponent("draft.bdw")
                if manager.fileExists(atPath: url.path) {
                    var isEndOfFile = false
                    let readHandle = try FileHandle(forReadingFrom: url)
                    var points = DataConverter.parse(dataToPoints: readHandle.readData(ofLength: Int(POINT_BUFFER_COUNT * LENGTH_SIZE)), withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height))
                    var previousPoint = CGPoint(x: -1, y: -1)
                    var currentPoint = previousPoint
                    while !points.isEmpty {
                        let point = points.removeFirst()
                        currentPoint = point.position
                        if bounds.contains(currentPoint) {
                            if point.action == .down {
                                let path = UIBezierPath()
                                path.lineJoinStyle = .round
                                path.lineCapStyle = .round
                                path.lineWidth = point.size
                                path.move(to: currentPoint)
                                path.addLine(to: currentPoint)
                                self.paths.append(
                                        Path(blendMode: point.type == .eraser ? .clear : .normal,
                                                bezierPath: path,
                                                points: [point],
                                                color: point.color))
                            } else {
                                let deltaX = abs(currentPoint.x - previousPoint.x)
                                let deltaY = abs(currentPoint.y - previousPoint.y)
                                if sqrt(deltaX * deltaX + deltaY * deltaY) < point.size / 2 {
                                    self.paths.last?.bezierPath.addLine(to: currentPoint)
                                } else {
                                    self.paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + previousPoint.x) / 2, y: (currentPoint.y + previousPoint.y) / 2), controlPoint: previousPoint)
                                }
                                self.paths.last?.points.append(point)
                                if point.action == .up {
                                    self.drawToPersistentImage(withBounds: bounds)
                                }
                            }
                            previousPoint = currentPoint
                        }
                        if points.count < POINT_BUFFER_COUNT / 2 && !isEndOfFile {
                            let maxByteCount = Int(POINT_BUFFER_COUNT * LENGTH_SIZE)
                            let data = readHandle.readData(ofLength: maxByteCount)
                            points.append(contentsOf: DataConverter.parse(dataToPoints: data, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
                            if data.count < maxByteCount {
                                readHandle.closeFile()
                                isEndOfFile = true
                            }
                        }
                    }
                }
            } catch {
                Logger.d("\(#function): \(error.localizedDescription)")
            }
            DispatchQueue.main.async {
                self.setNeedsDisplay()
                completionHandler()
            }
        }
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first {
            if lastTimestamp < 0 {
                lastTimestamp = touch.timestamp
            }
            let point = touch.location(in: self)
            if bounds.contains(point) {
                let path = UIBezierPath()
                path.lineJoinStyle = .round
                path.lineCapStyle = .round
                path.lineWidth = size
                path.move(to: point)
                let timestamp = touch.timestamp - lastTimestamp
                paths.append(
                        Path(blendMode: type == .eraser ? .clear : .normal,
                                bezierPath: path,
                                points: [Point(length: LENGTH_SIZE,
                                        function: .draw,
                                        position: point,
                                        color: color,
                                        action: .down,
                                        size: size,
                                        type: type,
                                        duration: timestamp > MAX_TIMESTAMP ? MAX_TIMESTAMP : timestamp)],
                                color: color))
            }
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let mainTouch = touches.first, let coalescedTouches = event?.coalescedTouches(for: mainTouch) {
            var points = [CGPoint]()
            if let lastPoint = paths.last?.bezierPath.currentPoint {
                points.append(lastPoint)
            }
            for touch in coalescedTouches {
                let point = touch.location(in: self)
                let previous = touch.previousLocation(in: self)
                if bounds.contains(point) && point != previous {
                    let deltaX = abs(point.x - previous.x)
                    let deltaY = abs(point.y - previous.y)
                    let timestamp = touch.timestamp - lastTimestamp
                    points.append(point)
                    if sqrt(deltaX * deltaX + deltaY * deltaY) < size / 2 {
                        paths.last?.bezierPath.addLine(to: point)
                    } else {
                        paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (point.x + previous.x) / 2, y: (point.y + previous.y) / 2), controlPoint: previous)
                    }
                    paths.last?.points.append(
                            Point(length: LENGTH_SIZE,
                                    function: .draw,
                                    position: point,
                                    color: color,
                                    action: .move,
                                    size: size,
                                    type: type,
                                    duration: timestamp > MAX_TIMESTAMP ? MAX_TIMESTAMP : timestamp))
                    lastTimestamp = touch.timestamp
                }
            }
            setNeedsDisplay(calculateRedrawRect(forPoints: points))
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let mainTouch = touches.first, let coalescedTouches = event?.coalescedTouches(for: mainTouch) {
            var points = [CGPoint]()
            if let lastPoint = paths.last?.bezierPath.currentPoint {
                points.append(lastPoint)
            }
            for touch in coalescedTouches {
                let point = touch.location(in: self)
                let previous = touch.previousLocation(in: self)
                if bounds.contains(point) {
                    let deltaX = abs(point.x - previous.x)
                    let deltaY = abs(point.y - previous.y)
                    let timestamp = touch.timestamp - lastTimestamp
                    points.append(point)
                    if sqrt(deltaX * deltaX + deltaY * deltaY) < size / 2 {
                        paths.last?.bezierPath.addLine(to: point)
                    } else {
                        paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (point.x + previous.x) / 2, y: (point.y + previous.y) / 2), controlPoint: previous)
                    }
                    paths.last?.points.append(
                            Point(length: LENGTH_SIZE,
                                    function: .draw,
                                    position: point,
                                    color: color,
                                    action: .up,
                                    size: size,
                                    type: type,
                                    duration: timestamp > MAX_TIMESTAMP ? MAX_TIMESTAMP : timestamp))
                    lastTimestamp = touch.timestamp
                }
            }
            setNeedsDisplay(calculateRedrawRect(forPoints: points))
            drawToPersistentImage(withBounds: bounds)
            redoPaths.removeAll()
            delegate?.canvasPathsDidChange()
        }
    }

    private func drawToPersistentImage(withBounds bounds: CGRect) {
        if paths.count > PATH_BUFFER_COUNT {
            var pointsToSave = [Point]()
            UIGraphicsBeginImageContextWithOptions(bounds.size, false, UIScreen.main.scale)
            persistentImage?.draw(in: bounds)
            let context = UIGraphicsGetCurrentContext()
            while paths.count > PATH_BUFFER_COUNT {
                let path = paths.removeFirst()
                if path.points.first?.type != .background {
                    context?.setBlendMode(path.blendMode)
                    path.color.setStroke()
                    path.bezierPath.stroke()
                } else {
                    persistentBackgroundColor = path.color
                }
                for point in path.points {
                    pointsToSave.append(point)
                }
            }
            persistentImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            writeHandle?.write(DataConverter.parse(pointsToData: pointsToSave, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
        }
    }

    private func calculateRedrawRect(forPoints points: [CGPoint]) -> CGRect {
        var minX: CGFloat = bounds.width
        var minY: CGFloat = bounds.height
        var maxX: CGFloat = 0
        var maxY: CGFloat = 0
        for point in points {
            minX = min(minX, point.x)
            minY = min(minY, point.y)
            maxX = max(maxX, point.x)
            maxY = max(maxY, point.y)
        }
        let origin = CGPoint(x: minX - size, y: minY - size)
        return CGRect(
                origin: CGPoint(x: minX - size, y: minY - size),
                size: CGSize(width: maxX - origin.x + size, height: maxY - origin.y + size))
    }

    func set(backgroundColor color: UIColor) {
        paths.append(Path(blendMode: .normal, bezierPath: UIBezierPath(), points: [Point(
                length: LENGTH_SIZE,
                function: .draw,
                position: .zero,
                color: color,
                action: .down,
                size: 0,
                type: .background,
                duration: 0)], color: color))
        drawToPersistentImage(withBounds: bounds)
        redoPaths.removeAll()
        delegate?.canvasPathsDidChange()
    }
}

protocol CanvasViewDelegate {
    func canvasPathsDidChange()
    func canvas(changeBackgroundColor color: UIColor)
}
