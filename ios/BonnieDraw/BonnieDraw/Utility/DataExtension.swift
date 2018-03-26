//
//  DataExtension.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 2016/12/1.
//  Copyright © 2016年 D-Link. All rights reserved.
//

import Foundation

extension Data {
    mutating func append(string: String) {
        if let data = string.data(using: .utf8) {
            append(data)
        }
    }

    func toInt() -> Int {
        return Int(self.withUnsafeBytes {
            (bytes: UnsafePointer<UInt8>) -> UInt in
            return bytes.withMemoryRebound(to: UInt.self, capacity: self.count, {
                ( point: UnsafePointer<UInt>) -> UInt in
                return point.pointee
            })
        })
    }

    func toAsciiString() -> String? {
        return String(data: self, encoding: String.Encoding.ascii)
    }
}
