//
//  Constant.swift
//  iOSTemplate
//
//  Created by Professor on 4/22/16.
//  Copyright © 2016 Professor. All rights reserved.
//

import UIKit

let DEBUG = Bundle.main.infoDictionary?["Configuration"] as? String == "Debug"
let PATH_BUFFER_COUNT: UInt16 = 20
let POINT_BUFFER_COUNT: UInt16 = 512
let MAX_TIMESTAMP: TimeInterval = 0.4
let LENGTH_SIZE: UInt16 = 20
let LENGTH_BYTE_SIZE = 2
let ERASER_COLOR = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
let SERVICE_DEVICE_TYPE = 2
let TOKEN_LIFETIME: TimeInterval = Double.greatestFiniteMagnitude
let UPDATE_INTERVAL: TimeInterval = 60.0

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

enum UserType: Int {
    case email = 1
    case facebook
    case google
    case twitter
}

enum FileType: Int {
    case png = 1
    case bdw
}

enum AccessControl: Int {
    case publicAccess = 1
    case contactAccess
    case privateAccess
}

struct Cell {
    static let BASIC = "basicCell"
    static let HOME = "homeCell"
    static let COLOR_PICKER = "colorPickerCell"
    static let SIZE_PICKER = "sizePickerCell"
    static let ACCOUNT_HEADER = "accountHeaderCell"
    static let ACCOUNT_GRID = "accountGridCell"
    static let ACCOUNT_LIST = "accountListCell"
    static let ACCOUNT_FOOTER = "accountFooterCell"
    static let FOLLOW = "followCell"
    static let RECOMMEND = "recommendCell"
}

struct Service {
    private static let BASE = "/bonniedraw_service/BDService"
    static let SCHEME = "https"
    static let HOST = "www.bonniedraw.com"
    static let LOGIN = "/login"
    static let FORGET_PASSWORD = "/forgetpwd"
    static let WORK_SAVE = "/worksSave"
    static let WORK_LIST = "/worksList"
    static let FILE_UPLOAD = "/fileUpload"
    static let USER_INFO_QUERY = "/userInfoQuery"
    static let USER_INFO_UPDATE = "/userInfoUpdate"
    static let LEAVE_MESSAGE = "/leavemsg"
    static let LIKE = "/setLike"
    static let FOLLOW = "/setFollowing"
    static let REPORT = "/setTurnin"
    static let UPDATE_PASSWORD = "/updatePwd"
    static let LOAD_FILE = "/loadFile"

    static func standard(withPath path: String) -> String {
        return "\(SCHEME)://\(HOST)\(BASE)\(path)"
    }

    static func filePath(withSubPath path: String?) -> String {
        guard let path = path else {
            return ""
        }
        return "\(SCHEME)://\(HOST)\(BASE)\(LOAD_FILE)\(path)"
    }
}

struct Identifier {
    static let PARENT = "parentViewController"
    static let HOME = "homeViewController"
    static let FOLLOW = "followViewController"
    static let NOTIFICATION = "notificationViewController"
}

struct Segue {
    static let ACCOUNT_EDIT = "accountEditSegue"
    static let SETTING = "settingSegue"
    static let PASSWORD = "passwordSegue"
    static let LANGUAGE = "languageSegue"
    static let DESCRIPTION = "descriptionSegue"
    static let WEB = "webSegue"
    static let COLLECTION = "collectionSegue"
    static let UPLOAD = "uploadSegue"
    static let DEBUG = "debugSegue"
    static let RECOMMEND = "recommendSegue"
}

struct Default {
    static let COLORS = "colors"
    static let TOKEN_TIMESTAMP = "tokenTimestamp"
    static let TOKEN = "token"
    static let USER_ID = "userId"
    static let EMAIL = "email"
    static let PASSWORD = "password"
    static let USER_TYPE = "userType"
    static let THIRD_PARTY_ID = "thirdPartyId"
    static let THIRD_PARTY_NAME = "thirdPartyName"
    static let THIRD_PARTY_EMAIL = "thirdPartyEmail"
    static let THIRD_PARTY_IMAGE = "thirdPartyImage"
}

struct Url {
    static let PRIVACY_POLICY = "https"
}
