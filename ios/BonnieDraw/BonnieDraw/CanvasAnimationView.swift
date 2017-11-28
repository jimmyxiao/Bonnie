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
    private var paths = [Path]()
    private var animatePoints = [Point]()
    private var persistentBackgroundColor: UIColor?
    private var persistentImage: UIImage?
    private var cacheBackgroundColor: UIColor?
    private var cacheImage: UIImage?
    private var cachePaths = [Path]()
    private var readHandle: FileHandle?
    private var timer: Timer?

    override func draw(_ rect: CGRect) {
        let context = UIGraphicsGetCurrentContext()
        cacheImage?.draw(in: rect)
        while !cachePaths.isEmpty {
            let path = cachePaths.removeFirst()
            if path.points.first?.type != .background {
                context?.setBlendMode(path.blendMode)
                path.color.setStroke()
                path.bezierPath.stroke()
            } else {
                cacheBackgroundColor = path.color
            }
        }
        if let cgImage = context?.makeImage() {
            cacheImage = UIImage(cgImage: cgImage)
        }
        delegate?.canvasAnimation(changeBackgroundColor: cacheBackgroundColor ?? persistentBackgroundColor ?? .white)
        if paths.last?.blendMode == .clear,
           let point = paths.last?.points.last,
           point.action == .move {
            context?.setBlendMode(.normal)
            UIColor.black.setStroke()
            UIBezierPath(arcCenter: point.position, radius: point.size / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        }
        if !animatePoints.isEmpty {
            animate()
        } else if !paths.isEmpty {
            delegate?.canvasAnimationDidFinishAnimation()
        }
    }

    func load(completionHandler: @escaping () -> Void) {
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
                                if hypot(currentPoint.x - previousPoint.x, currentPoint.y - previousPoint.y) < point.size / 3 {
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
                    self.cacheImage = self.persistentImage
                    self.cachePaths = self.paths
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

    func play() {
        if animatePoints.isEmpty {
            persistentBackgroundColor = nil
            persistentImage = nil
            cacheBackgroundColor = nil
            cacheImage = nil
            cachePaths.removeAll()
            paths.removeAll()
            do {
                timer?.invalidate()
                if let url = url {
                    let readHandle = try FileHandle(forReadingFrom: url)
                    animatePoints.append(
                            contentsOf: DataConverter.parse(
                                    dataToPoints: readHandle.readData(ofLength: Int(POINT_BUFFER_COUNT * LENGTH_SIZE)),
                                    withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
                    self.readHandle = readHandle
                }
                if !animatePoints.isEmpty {
                    animate()
                } else {
                    delegate?.canvasAnimationFileParseError()
                }
            } catch {
                Logger.d("\(#function): \(error.localizedDescription)")
            }
        }
        setNeedsDisplay()
    }

    func pause() {
        timer?.invalidate()
    }

    private func animate() {
        let point = animatePoints.removeFirst()
        if bounds.contains(point.position) {
            if point.action == .down {
                let path = UIBezierPath()
                path.lineJoinStyle = .round
                path.lineCapStyle = .round
                path.lineWidth = point.size
                path.move(to: point.position)
                let newPath = Path(blendMode: point.type == .eraser ? .clear : .normal,
                        bezierPath: path,
                        points: [point],
                        color: point.color)
                paths.append(newPath)
                cachePaths.append(newPath)
            } else if let previous = paths.last?.points.last?.position {
                if cachePaths.isEmpty,
                   let startPosition = paths.last?.bezierPath.currentPoint,
                   let startPoint = paths.last?.points.last {
                    let path = UIBezierPath()
                    path.lineJoinStyle = .round
                    path.lineCapStyle = .round
                    path.lineWidth = point.size
                    path.move(to: startPosition)
                    cachePaths.append(Path(blendMode: point.type == .eraser ? .clear : .normal,
                            bezierPath: path,
                            points: [startPoint],
                            color: point.color))
                }
                if hypot(point.position.x - previous.x, point.position.y - previous.y) < point.size / 3 {
                    paths.last?.bezierPath.addLine(to: point.position)
                    cachePaths.last?.bezierPath.addLine(to: point.position)
                } else {
                    paths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (point.position.x + previous.x) / 2, y: (point.position.y + previous.y) / 2), controlPoint: previous)
                    cachePaths.last?.bezierPath.addQuadCurve(to: CGPoint(x: (point.position.x + previous.x) / 2, y: (point.position.y + previous.y) / 2), controlPoint: previous)
                }
                paths.last?.points.append(point)
                cachePaths.last?.points.append(point)
                if point.action == .up {
                    drawToPersistentImage(withBounds: bounds)
                }
            }
            timer?.invalidate()
            timer = Timer.scheduledTimer(withTimeInterval: point.duration, repeats: false) {
                timer in
                self.setNeedsDisplay()
            }
        }
        if let readHandle = readHandle, animatePoints.count < POINT_BUFFER_COUNT / 2 {
            let maxByteCount = Int(POINT_BUFFER_COUNT * LENGTH_SIZE)
            let data = readHandle.readData(ofLength: maxByteCount)
            animatePoints.append(contentsOf: DataConverter.parse(dataToPoints: data, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
            if data.count < maxByteCount {
                readHandle.closeFile()
                self.readHandle = nil
            }
        }
    }

    private func drawToPersistentImage(withBounds bounds: CGRect) {
        if paths.count > PATH_BUFFER_COUNT {
            var pointsToSave = [Point]()
            UIGraphicsBeginImageContextWithOptions(bounds.size, false, contentScaleFactor)
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
    func canvasAnimation(changeBackgroundColor color: UIColor)
}
