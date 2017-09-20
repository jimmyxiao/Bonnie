//
//  BundleExtension.swift
//  Native
//
//  Created by Jason Hsu 08329 on 2/13/17.
//  Copyright © 2017 D-LINK. All rights reserved.
//

import UIKit

extension Bundle {
    func loadView(from xibName: String) -> UIView? {
        let objects = loadNibNamed(xibName, owner: self, options: nil)
        if let objects = objects {
            for object in objects {
                if let view = object as? UIView {
                    return view
                }
            }
        }
        return nil
    }
}
