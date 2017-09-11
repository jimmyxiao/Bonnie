//
//  Constant.swift
//  iOSTemplate
//
//  Created by Professor on 4/22/16.
//  Copyright © 2016 Professor. All rights reserved.
//

import UIKit

let DEBUG = Bundle.main.infoDictionary?["Configuration"] as? String == "Debug"

enum Function: UInt16 {
    case draw = 0xa101
}

enum Action: UInt8 {
    case down
    case up
    case move
}

enum Type: UInt8 {
    case round
}
