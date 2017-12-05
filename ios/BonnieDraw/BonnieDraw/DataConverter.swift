//
//  DataConverter.swift
//  BonnieDraw
//
//  Created by Professor on 03/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class DataConverter: NSObject {
    static func parse(dataToPoints data: Data, withScale scale: CGFloat) -> [Point] {
        var points = [Point]()
        guard data.count % Int(LENGTH_SIZE) == 0 else {
            return points
        }
        if !data.isEmpty {
            let byteMax = CGFloat(UInt8.max)
            var bytes = [UInt8](data)
            while !bytes.isEmpty {
                points.append(Point(
                        length: UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8,
                        function: Function(rawValue: UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) ?? .draw,
                        position: CGPoint(
                                x: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / scale,
                                y: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / scale),
                        color: color(blue: CGFloat(bytes.removeFirst()) / byteMax,
                                green: CGFloat(bytes.removeFirst()) / byteMax,
                                red: CGFloat(bytes.removeFirst()) / byteMax,
                                alpha: CGFloat(bytes.removeFirst()) / byteMax),
                        action: Action(rawValue: bytes.removeFirst()) ?? .move,
                        size: CGFloat(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) * 2 / scale,
                        type: Type(rawValue: bytes.removeFirst()) ?? .pen,
                        duration: TimeInterval(Double(UInt16(bytes.removeFirst()) + UInt16(bytes.removeFirst()) << 8) / 1000)))
                bytes.removeFirst()
                bytes.removeFirst()
            }
        }
        return points
    }

    static func parse(pointsToData points: [Point], withScale scale: CGFloat) -> Data {
        var bytes = [UInt8]()
        if !points.isEmpty {
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
                bytes.append(UInt8(ciColor.blue * byteMax))
                bytes.append(UInt8(ciColor.green * byteMax))
                bytes.append(UInt8(ciColor.red * byteMax))
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

    private static func color(blue: CGFloat, green: CGFloat, red: CGFloat, alpha: CGFloat) -> UIColor {
        return UIColor(red: red, green: green, blue: blue, alpha: alpha)
    }
}
