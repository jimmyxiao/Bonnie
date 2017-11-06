//
//  CanvasAnimationView.swift
//  BonnieDraw
//
//  Created by Professor on 03/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasAnimationView: UIView {
    var delegate: CanvasAnimationViewDelegate?
    var url: URL?
    var persistentImage: UIImage?
    private var readHandle: FileHandle?
    private var controlPoint = CGPoint.zero, currentPoint = CGPoint.zero
    private var paths = [Path]()
    private var cachePoints = [Point]()
    private var timer: Timer?

    override func draw(_ rect: CGRect) {
        persistentImage?.draw(in: bounds)
        for path in paths {
            path.color.setStroke()
            path.bezierPath.stroke()
        }
        if let point = paths.last?.points.last,
           point.action == .move,
           point.color == ERASER_COLOR {
            UIColor.black.setStroke()
            UIBezierPath(arcCenter: point.position, radius: point.size / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        }
        if !cachePoints.isEmpty {
            animate()
        } else if timer != nil {
            delegate?.canvasAnimationDidFinishAnimation()
        }
    }

    func load(completionHandler: (() -> Void)? = nil) {
        let bounds = self.bounds
        DispatchQueue.global().async {
            do {
                let manager = FileManager.default
                let url = try self.url ?? manager.url(
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
                                        self.drawToPersistentImage(withBounds: bounds)
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
                completionHandler?()
            }
        }
    }

    func play() {
        if cachePoints.isEmpty {
            paths.removeAll()
            do {
                timer?.invalidate()
                if let url = url {
                    let readHandle = try FileHandle(forReadingFrom: url)
                    cachePoints.append(
                            contentsOf: DataConverter.parse(
                                    dataToPoints: readHandle.readData(ofLength: Int(POINT_BUFFER_COUNT * LENGTH_SIZE)),
                                    withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
                    self.readHandle = readHandle
                }
                if !cachePoints.isEmpty {
                    let point = cachePoints.removeFirst()
                    currentPoint = point.position
                    controlPoint = currentPoint
                    if bounds.contains(currentPoint) {
                        let path = UIBezierPath()
                        path.move(to: currentPoint)
                        path.lineCapStyle = .round
                        path.lineWidth = point.size
                        paths.append(
                                Path(bezierPath: path,
                                        points: [point],
                                        color: point.color))
                        persistentImage = nil
                        setNeedsDisplay()
                    }
                } else {
                    delegate?.canvasAnimationFileParseError()
                }
            } catch {
                Logger.d("\(#function): \(error.localizedDescription)")
            }
        } else {
            setNeedsDisplay()
        }
    }

    func pause() {
        timer?.invalidate()
    }

    func stop() {
        timer?.invalidate()
        paths.removeAll()
        cachePoints.removeAll()
        persistentImage = nil
        setNeedsDisplay()
    }

    private func animate() {
        if !cachePoints.isEmpty {
            let point = cachePoints.removeFirst()
            if bounds.contains(point.position) {
                currentPoint = point.position
                if point.action != .down {
                    var points = [CGPoint]()
                    if let lastPoint = paths.last?.bezierPath.currentPoint {
                        points.append(lastPoint)
                    }
                    points.append(point.position)
                    paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (currentPoint.x + controlPoint.x) / 2, y: (currentPoint.y + controlPoint.y) / 2), controlPoint: controlPoint)
                    paths.last?.points.append(point)
                    if point.action == .up {
                        drawToPersistentImage(withBounds: bounds)
                    }
                    timer = Timer.scheduledTimer(withTimeInterval: point.duration, repeats: false) {
                        timer in
                        self.setNeedsDisplay(self.calculateRedrawRect(forPoints: points, withSize: point.size))
                    }
                    controlPoint = currentPoint
                } else {
                    controlPoint = currentPoint
                    let path = UIBezierPath()
                    path.move(to: currentPoint)
                    path.lineCapStyle = .round
                    path.lineWidth = point.size
                    paths.append(
                            Path(bezierPath: path,
                                    points: [point],
                                    color: point.color))
                    animate()
                }
            }
            if let readHandle = readHandle, cachePoints.count < POINT_BUFFER_COUNT / 2 {
                let maxByteCount = Int(POINT_BUFFER_COUNT * LENGTH_SIZE)
                let data = readHandle.readData(ofLength: maxByteCount)
                cachePoints.append(contentsOf: DataConverter.parse(dataToPoints: data, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
                if data.count < maxByteCount {
                    readHandle.closeFile()
                    self.readHandle = nil
                }
            }
        }
    }

    private func drawToPersistentImage(withBounds bounds: CGRect) {
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
        }
    }

    private func calculateRedrawRect(forPoints points: [CGPoint], withSize size: CGFloat) -> CGRect {
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

protocol CanvasAnimationViewDelegate {
    func canvasAnimationDidFinishAnimation()

    func canvasAnimationFileParseError()
}
