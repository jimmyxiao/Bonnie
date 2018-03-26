//
// Created by Jason Hsu 08329 on 1/13/16.
// Copyright (c) 2016 Professor. All rights reserved.
//

import Foundation

class Logger {
    static func p(_ msg: Any) {
        if DEBUG {
            print(Bundle.main.bundleIdentifier! + " \(msg)")
        }
    }

    static func d(_ msg: Any) {
        if DEBUG {
            debugPrint(Bundle.main.bundleIdentifier! + " \(msg)")
        }
    }

    static func l(_ msg: Any) {
        if DEBUG {
            NSLog(Bundle.main.bundleIdentifier!, " \(msg)")
        }
    }
}
