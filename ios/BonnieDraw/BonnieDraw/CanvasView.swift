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
    var persistentImage: UIImage?
    private var lastTimestamp: TimeInterval = -1
    private var url = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("cache.bdw")
    private var controlPoint = CGPoint.zero, currentPoint = CGPoint.zero
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

    override func draw(_ rect: CGRect) {
        persistentImage?.draw(in: bounds)
        for path in paths {
            path.color.setStroke()
            path.bezierPath.stroke()
        }
        if color == ERASER_COLOR,
           let point = paths.last?.points.last,
           point.action == .move {
            UIColor.black.setStroke()
            UIBezierPath(arcCenter: point.position, radius: point.size / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        }
    }

    func reset() {
        writeHandle?.closeFile()
        paths.removeAll()
        redoPaths.removeAll()
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
                    if !points.isEmpty {
                        var point = points.removeFirst()
                        if bounds.contains(point.position) {
                            self.currentPoint = point.position
                            self.controlPoint = self.currentPoint
                            let path = UIBezierPath()
                            path.move(to: self.currentPoint)
                            path.lineCapStyle = .round
                            path.lineWidth = point.size
                            self.paths.append(
                                    Path(bezierPath: path,
                                            points: [point],
                                            color: point.color))
                            while !points.isEmpty {
                                point = points.removeFirst()
                                self.currentPoint = point.position
                                if point.action != .down {
                                    self.paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (self.currentPoint.x + self.controlPoint.x) / 2, y: (self.currentPoint.y + self.controlPoint.y) / 2), controlPoint: self.controlPoint)
                                    self.paths.last?.points.append(point)
                                    if point.action == .up {
                                        self.drawToPersistentImage(withSize: bounds.size)
                                    }
                                    self.controlPoint = self.currentPoint
                                } else {
                                    self.controlPoint = self.currentPoint
                                    let path = UIBezierPath()
                                    path.move(to: self.currentPoint)
                                    path.lineCapStyle = .round
                                    path.lineWidth = point.size
                                    self.paths.append(
                                            Path(bezierPath: path,
                                                    points: [point],
                                                    color: point.color))
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
            currentPoint = touch.location(in: self)
            controlPoint = currentPoint
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
            var points = [CGPoint]()
            if let lastPoint = paths.last?.bezierPath.currentPoint {
                points.append(lastPoint)
            }
            for touch in coalescedTouches {
                currentPoint = touch.location(in: self)
                points.append(currentPoint)
                if bounds.contains(currentPoint) && currentPoint != controlPoint {
                    paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + controlPoint.x) / 2, y: (currentPoint.y + controlPoint.y) / 2), controlPoint: controlPoint)
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
                    controlPoint = currentPoint
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
                currentPoint = touch.location(in: self)
                points.append(currentPoint)
                let timestamp = touch.timestamp - lastTimestamp
                if bounds.contains(currentPoint) {
                    if currentPoint != controlPoint {
                        paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + controlPoint.x) / 2, y: (currentPoint.y + controlPoint.y) / 2), controlPoint: controlPoint)
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
                        controlPoint = currentPoint
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
            drawToPersistentImage(withSize: bounds.size)
            setNeedsDisplay(calculateRedrawRect(forPoints: points))
            redoPaths.removeAll()
            delegate?.canvasPathsDidChange()
        }
    }

    private func drawToPersistentImage(withSize size: CGSize) {
        if paths.count > PATH_BUFFER_COUNT {
            var pointsToSave = [Point]()
            UIGraphicsBeginImageContextWithOptions(size, false, UIScreen.main.scale)
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
}

protocol CanvasViewDelegate {
    func canvasPathsDidChange()
}
