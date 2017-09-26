//
//  Constant.swift
//  iOSTemplate
//
//  Created by Professor on 4/22/16.
//  Copyright © 2016 Professor. All rights reserved.
//

import UIKit

let DEBUG = Bundle.main.infoDictionary?["Configuration"] as? String == "Debug"
let LENGTH_SIZE: UInt16 = 20
let LENGTH_BYTE_SIZE = 2

enum Function: UInt16 {
    case draw = 0xa101
}

enum Action: UInt8 {
    case down = 1
    case up
    case move
}

enum Type: UInt8 {
    case round
}

struct Cell {
    static let BASIC = "basicCell"
    static let HOME = "homeCell"
}

struct Service {
    private static let BASE = "/bonniedraw_service/BDService"
    static let SCHEME = "https"
    static let HOST = "www.bonniedraw.com"
    static let LOGIN = "\(BASE)/login"
    static let FORGET_PASSWORD = "\(BASE)/forgetpwd"
    static let WORK_SAVE = "\(BASE)/worksSave"
    static let WORK_LIST = "\(BASE)/worksList"
    static let FILE_UPLOAD = "\(BASE)/fileUpload"
    static let USER_INFO_QUERY = "\(BASE)/userInfoQuery"
    static let USER_INFO_UPDATE = "\(BASE)/userInfoUpdate"
    static let LEAVE_MESSAGE = "\(BASE)/leavemsg"
    static let LIKE = "\(BASE)/setLike"
    static let FOLLOW = "\(BASE)/setFollowing"
    static let REPORT = "\(BASE)/setTurnin"
    static let UPDATE_PASSWORD = "\(BASE)/updatePwd"
}

struct Identifier {
}

struct Segue {
}

struct Default {
    static let TOKEN = "token"
    static let USER_ID = "userId"
    static let THIRD_PARTY_ID = "thirdPartyId"
    static let THIRD_PARTY_NAME = "thirdPartyName"
    static let THIRD_PARTY_IMAGE = "thirdPartyImage"
}
