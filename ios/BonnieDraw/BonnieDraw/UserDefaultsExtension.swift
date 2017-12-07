//
//  UserDefaultsExtension.swift
//  BonnieDraw
//
//  Created by Professor on 15/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

extension UserDefaults {
    func colors(forKey key: String) -> [UIColor]? {
        if let datas = array(forKey: key) as? [Data] {
            var colors = [UIColor]()
            for data in datas {
                if let color = NSKeyedUnarchiver.unarchiveObject(with: data) as? UIColor {
                    colors.append(color)
                }
            }
            return colors
        }
        return nil
    }

    func color(forKey key: String) -> UIColor? {
        if let data = data(forKey: key) {
            return NSKeyedUnarchiver.unarchiveObject(with: data) as? UIColor
        }
        return nil
    }

    func set(colors: [UIColor]?, forKey key: String) {
        if let colors = colors {
            var datas = [Data]()
            for color in colors {
                datas.append(NSKeyedArchiver.archivedData(withRootObject: color))
            }
            set(datas, forKey: key)
        } else {
            removeObject(forKey: key)
        }
    }

    func set(color: UIColor?, forKey key: String) {
        if let color = color {
            set(NSKeyedArchiver.archivedData(withRootObject: color), forKey: key)
        } else {
            removeObject(forKey: key)
        }
    }
}
