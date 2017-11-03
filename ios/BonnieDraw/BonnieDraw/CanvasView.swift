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
    private var url: URL?
    private var controlPoint = CGPoint.zero, currentPoint = CGPoint.zero
    private var writeHandle: FileHandle?
    private var readHandle: FileHandle?

    override func awakeFromNib() {
        do {
            url = try FileManager.default.url(
                    for: .documentationDirectory,
                    in: .userDomainMask,
                    appropriateFor: nil,
                    create: true).appendingPathComponent("cache.bdw")
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
        paths.removeAll()
        redoPaths.removeAll()
        persistentImage = nil
        delegate?.canvasPathsDidChange()
        do {
            if let url = url {
                let manager = FileManager.default
                if manager.fileExists(atPath: url.path) {
                    writeHandle?.closeFile()
                    try manager.removeItem(at: url)
                }
                manager.createFile(atPath: url.path, contents: nil, attributes: nil)
                writeHandle = try FileHandle(forWritingTo: url)
            }
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
        }
        setNeedsDisplay()
        lastTimestamp = -1
    }

    func save() {
        var points = [Point]()
        while !paths.isEmpty {
            points.append(contentsOf: paths.removeFirst().points)
        }
        redoPaths.removeAll()
        persistentImage = nil
        writeHandle?.write(DataConverter.parse(pointsToData: points, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
        writeHandle?.closeFile()
        setNeedsDisplay()
        delegate?.canvasPathsDidChange()
    }

    func saveForUpload() -> URL? {
        guard let url = url else {
            return nil
        }
        do {
            let manager = FileManager.default
            let uploadUrl = try manager.url(
                    for: .documentationDirectory,
                    in: .userDomainMask,
                    appropriateFor: nil,
                    create: true).appendingPathComponent("upload.bdw")
            if manager.fileExists(atPath: uploadUrl.path) {
                try manager.removeItem(at: uploadUrl)
            }
            try manager.copyItem(at: url, to: uploadUrl)
            let writeHandle = try FileHandle(forWritingTo: uploadUrl)
            writeHandle.seekToEndOfFile()
            var points = [Point]()
            for path in paths {
                points.append(contentsOf: path.points)
            }
            writeHandle.write(DataConverter.parse(pointsToData: points, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
            writeHandle.closeFile()
            return uploadUrl
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
        }
        return nil
    }

    func load() {
        do {
            guard let url = url else {
                return
            }
            let manager = FileManager.default
            if manager.fileExists(atPath: url.path) {
                let data = try Data(contentsOf: url)
                if !data.isEmpty {
                    try manager.removeItem(at: url)
                    manager.createFile(atPath: url.path, contents: nil, attributes: nil)
                    writeHandle = try FileHandle(forWritingTo: url)
                    for point in DataConverter.parse(dataToPoints: data, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)) {
                        if bounds.contains(currentPoint) {
                            currentPoint = point.position
                            if point.action != .down {
                                paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + controlPoint.x) / 2, y: (currentPoint.y + controlPoint.y) / 2), controlPoint: controlPoint)
                                paths.last?.points.append(point)
                                if point.action == .up {
                                    drawToPersistentImage(saveToFile: true)
                                }
                            } else {
                                let path = UIBezierPath()
                                path.move(to: currentPoint)
                                path.lineCapStyle = .round
                                path.lineWidth = point.size
                                paths.append(
                                        Path(bezierPath: path,
                                                points: [point],
                                                color: point.color))
                            }
                            controlPoint = currentPoint
                        }
                    }
                    setNeedsDisplay()
                    delegate?.canvasPathsDidChange()
                } else {
                    writeHandle = try FileHandle(forWritingTo: url)
                }
            } else {
                manager.createFile(atPath: url.path, contents: nil, attributes: nil)
                writeHandle = try FileHandle(forWritingTo: url)
            }
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
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
            drawToPersistentImage(saveToFile: true)
            setNeedsDisplay(calculateRedrawRect(forPoints: points))
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
