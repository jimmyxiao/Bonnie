//
//  ColorExtension.swift
//  Native
//
//  Created by Jason Hsu 08329 on 1/11/17.
//  Copyright © 2017 D-LINK. All rights reserved.
//

import UIKit

extension UIColor {
    static func getDefaultColors() -> [UIColor] {
        return [UIColor(red: 1.00, green: 0.07, blue: 0.00, alpha: 1.0),
                UIColor(red: 0.91, green: 0.12, blue: 0.39, alpha: 1.0),
                UIColor(red: 0.61, green: 0.15, blue: 0.69, alpha: 1.0),
                UIColor(red: 0.40, green: 0.23, blue: 0.72, alpha: 1.0),
                UIColor(red: 0.25, green: 0.32, blue: 0.71, alpha: 1.0),
                UIColor(red: 0.13, green: 0.59, blue: 0.95, alpha: 1.0),
                UIColor(red: 0.01, green: 0.66, blue: 0.96, alpha: 1.0),
                UIColor(red: 0.00, green: 0.74, blue: 0.83, alpha: 1.0),
                UIColor(red: 0.00, green: 0.59, blue: 0.53, alpha: 1.0),
                UIColor(red: 0.30, green: 0.69, blue: 0.31, alpha: 1.0),
                UIColor(red: 0.55, green: 0.76, blue: 0.29, alpha: 1.0),
                UIColor(red: 0.80, green: 0.86, blue: 0.22, alpha: 1.0),
                UIColor(red: 1.00, green: 0.92, blue: 0.23, alpha: 1.0),
                UIColor(red: 1.00, green: 0.76, blue: 0.03, alpha: 1.0),
                UIColor(red: 1.00, green: 0.60, blue: 0.00, alpha: 1.0),
                UIColor(red: 1.00, green: 0.34, blue: 0.13, alpha: 1.0),
                UIColor(red: 0.00, green: 0.00, blue: 0.00, alpha: 1.0),
                UIColor(red: 1.00, green: 1.00, blue: 1.00, alpha: 1.0)]
    }

    static func getTextColor() -> UIColor {
        return UIColor(red: 0.27, green: 0.20, blue: 0.20, alpha: 1.0)
    }

    static func getAccentColor() -> UIColor {
        return UIColor(red: 0.95, green: 0.44, blue: 0.27, alpha: 1.0)
    }

    convenience init?(hex: String) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")
        var rgb: UInt32 = 0
        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0
        let length = hexSanitized.count
        guard Scanner(string: hexSanitized).scanHexInt32(&rgb) else {
            return nil
        }
        if length == 6 {
            r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
            g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
            b = CGFloat(rgb & 0x0000FF) / 255.0
        } else if length == 8 {
            r = CGFloat((rgb & 0xFF000000) >> 24) / 255.0
            g = CGFloat((rgb & 0x00FF0000) >> 16) / 255.0
            b = CGFloat((rgb & 0x0000FF00) >> 8) / 255.0
            a = CGFloat(rgb & 0x000000FF) / 255.0
        } else {
            return nil
        }
        self.init(red: r, green: g, blue: b, alpha: a)
    }

    func toHex(alpha: Bool = false) -> String? {
        guard let components = cgColor.components, components.count >= 3 else {
            return nil
        }
        let r = Float(components[0])
        let g = Float(components[1])
        let b = Float(components[2])
        var a = Float(1.0)
        if components.count >= 4 {
            a = Float(components[3])
        }
        if alpha {
            return String(format: "%02lX%02lX%02lX%02lX", lroundf(r * 255), lroundf(g * 255), lroundf(b * 255), lroundf(a * 255))
        } else {
            return String(format: "%02lX%02lX%02lX", lroundf(r * 255), lroundf(g * 255), lroundf(b * 255))
        }
    }
}
