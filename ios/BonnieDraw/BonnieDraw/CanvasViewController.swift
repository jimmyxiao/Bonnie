//
//  CanvasViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasViewController: UIViewController {
    @IBOutlet weak var mainImageView: UIImageView!
    @IBOutlet weak var tempImageView: UIImageView!
    var lastPoint = CGPoint.zero
    var size: CGFloat = 10
    var alpha: CGFloat = 1
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    var swiped = false
    var url: URL?
    var handle: FileHandle?
    var points = [Point]()

    override func viewDidLoad() {
        do {
            let manager = FileManager.default
            let url = try manager.url(for: .documentationDirectory, in: .userDomainMask, appropriateFor: nil, create: true).appendingPathComponent("temp.bdw")
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

    @IBAction func reset(_ sender: AnyObject) {
        mainImageView.image = nil
    }

    @IBAction func share(_ sender: UIButton) {
        var items = [Any]()
        if let url = url {
            items.append(url)
        }
        let controller = UIActivityViewController(activityItems: items, applicationActivities: nil)
        controller.excludedActivityTypes = [.airDrop, .saveToCameraRoll, .assignToContact, .addToReadingList, .copyToPasteboard, .print]
        present(controller, animated: true)
        if let presentation = controller.popoverPresentationController {
            presentation.sourceView = sender
            presentation.sourceRect = sender.bounds
        }
    }

    private func savePointsToFile() {
        let scale = (CGFloat(UInt16.max) + 1) / min(view.bounds.width, view.bounds.height)
        let byteMax = CGFloat(UInt8.max)
        var bytes = [UInt8]()
        for point in points {
            bytes.append(UInt8(point.length & 0x00ff))
            bytes.append(UInt8(point.length >> 8))
            bytes.append(UInt8(point.function.rawValue & 0x00ff))
            bytes.append(UInt8(point.function.rawValue >> 8))
            let scaledX = UInt16(CGFloat(point.x) * scale)
            bytes.append(UInt8(scaledX & 0x00ff))
            bytes.append(UInt8(scaledX >> 8))
            let scaledY = UInt16(CGFloat(point.y) * scale)
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
            bytes.append(0)
            bytes.append(0)
        }
        handle?.write(Data(bytes: bytes))
        points.removeAll()
    }

    private func drawLineFrom(fromPoint: CGPoint, toPoint: CGPoint) {
        UIGraphicsBeginImageContext(tempImageView.bounds.size)
        let context = UIGraphicsGetCurrentContext()
        tempImageView.image?.draw(in: tempImageView.bounds)
        context?.move(to: fromPoint)
        context?.addLine(to: toPoint)
        context?.setLineCap(.round)
        context?.setLineWidth(size)
        context?.setStrokeColor(red: red, green: green, blue: blue, alpha: 1)
        context?.setBlendMode(.normal)
        context?.strokePath()
        tempImageView.image = UIGraphicsGetImageFromCurrentImageContext()
        tempImageView.alpha = alpha
        UIGraphicsEndImageContext()
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        swiped = false
        if let touch = touches.first {
            lastPoint = touch.location(in: view)
            points.append(
                    Point(length: 18,
                            function: .draw,
                            x: lastPoint.x,
                            y: lastPoint.y,
                            color: (alpha, red, green, blue),
                            action: .down,
                            size: size,
                            type: .round))
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        swiped = true
        if let touch = touches.first {
            let currentPoint = touch.location(in: view)
            drawLineFrom(fromPoint: lastPoint, toPoint: currentPoint)
            lastPoint = currentPoint
            points.append(
                    Point(length: 18,
                            function: .draw,
                            x: lastPoint.x,
                            y: lastPoint.y,
                            color: (alpha, red, green, blue),
                            action: .move,
                            size: size,
                            type: .round))
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if !swiped {
            drawLineFrom(fromPoint: lastPoint, toPoint: lastPoint)
            points.append(
                    Point(length: 18,
                            function: .draw,
                            x: lastPoint.x,
                            y: lastPoint.y,
                            color: (alpha, red, green, blue),
                            action: .up,
                            size: size,
                            type: .round))
        }
        UIGraphicsBeginImageContext(mainImageView.bounds.size)
        mainImageView.image?.draw(in: mainImageView.bounds, blendMode: .normal, alpha: 1)
        tempImageView.image?.draw(in: mainImageView.bounds, blendMode: .normal, alpha: alpha)
        mainImageView.image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        tempImageView.image = nil
        savePointsToFile()
    }
}
