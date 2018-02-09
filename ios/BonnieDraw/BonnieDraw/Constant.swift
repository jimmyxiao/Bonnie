//
//  Constant.swift
//  iOSTemplate
//
//  Created by Professor on 4/22/16.
//  Copyright © 2016 Professor. All rights reserved.
//

import UIKit

let DEBUG = Bundle.main.infoDictionary?["Configuration"] as? String == "Debug"
let POINT_BUFFER_COUNT: UInt16 = 512
let LENGTH_SIZE: UInt16 = 20
let LENGTH_BYTE_SIZE = 2
let SERVICE_DEVICE_TYPE = 2
let TOKEN_LIFETIME: TimeInterval = Double.greatestFiniteMagnitude
let UPDATE_INTERVAL: TimeInterval = Double.greatestFiniteMagnitude
let ANIMATION_TIMER: TimeInterval = 1.0 / 60.0

enum Function: UInt16 {
    case draw = 0xa101
}

enum Action: UInt8 {
    case down = 1
    case up
    case move
}

enum Type: UInt8 {
    case eraser
    case crayon
    case pencil
    case pen
    case marker
    case airbrush
    case background
}

enum UserType: Int {
    case email = 1
    case facebook
    case google
    case twitter
}

enum UserGroup: Int {
    case personal
    case business
}

enum FileType: Int {
    case png = 1
    case bdw
}

enum FollowingType: Int {
    case follow = 1
    case fan
}

enum NotificationType: Int {
    case followed = 1
    case joined
    case commented
    case posted
    case liked
}

enum AccessControl: Int {
    case publicAccess = 1
    case contactAccess
    case privateAccess
}

enum ReportType: Int {
    case sexual = 1
    case violence
    case other = 99
}

enum Gender: Int {
    case male = 1
    case female
    case unspecified
}

struct FileUrl {
    static let CACHE = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("cache.bdw")
    static let DRAFT = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("draft.bdw")
    static let RESULT = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("result.bdw")
    static let INK = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("ink.png")
    static let THUMBNAIL = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("thumbnail.png")
    static let STATE = try! FileManager.default.url(
            for: .documentationDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true).appendingPathComponent("state.plist")
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
    static let USER = "userCell"
    static let NOTIFICATION = "notificationCell"
    static let CANVAS_SETTING = "canvasSettingCell"
    static let BRUSH_PICKER = "brushPickerCell"
    static let COMMENT = "commentCell"
    static let SWITCH = "switchCell"
}

struct Service {
    static let BASE = "/bonniedraw_service/BDService"
    static let SCHEME = "https"
    static let HOST = "www.bonniedraw.com"
    static let LOGIN = "/login"
    static let FORGET_PASSWORD = "/forgetpwd"
    static let NOTIFICATION = "/notiMsg"
    static let WORK_SAVE = "/worksSave"
    static let WORK_LIST = "/worksList"
    static let WORK_DELETE = "/deleteWork"
    static let FILE_UPLOAD = "/fileUpload"
    static let USER_INFO_QUERY = "/userInfoQuery"
    static let USER_INFO_UPDATE = "/userInfoUpdate"
    static let LEAVE_MESSAGE = "/leavemsg"
    static let SET_LIKE = "/setLike"
    static let SET_FOLLOW = "/setFollowing"
    static let SET_COLLECTION = "/setCollection"
    static let SET_TURN_IN = "/setTurnin"
    static let REPORT = "/setTurnin"
    static let UPDATE_PASSWORD = "/updatePwd"
    static let LOAD_FILE = "/loadFile"
    static let SOCIAL_SHARE = "/socialShare"
    static let FOLLOWING_LIST = "/followingList"
    static let TAG_LIST = "/tagList"
    static let FRIEND_LIST = "/friendsList"
    static let ABOUT = "https://www.bonniedraw.com/#/about_app"
    static let PRIVACY_POLICY = "https://www.bonniedraw.com/BonnieDrawClient/#/privacy_app"
    static let TERM_OF_USE = "https://www.bonniedraw.com/BonnieDrawClient/#/terms_app"

    static func standard(withPath path: String) -> String {
        return "\(SCHEME)://\(HOST)\(BASE)\(path)"
    }

    static func filePath(withSubPath path: String?) -> String {
        guard let path = path else {
            return ""
        }
        return "\(SCHEME)://\(HOST)\(BASE)\(LOAD_FILE)\(path)"
    }

    static func sharePath(withId id: Int?) -> String {
        guard let id = id else {
            return ""
        }
        return "\(SCHEME)://\(HOST)\(BASE)\(SOCIAL_SHARE)?id=\(id)"
    }
}

struct Identifier {
    static let PARENT = "parentViewController"
    static let HOME = "homeViewController"
    static let FOLLOW = "followViewController"
    static let NOTIFICATION = "notificationViewController"
    static let REPORT = "reportViewController"
    static let WORK = "workViewController"
    static let COMMENT = "commentViewController"
    static let NAVIGATION = "navigationViewController"
}

struct Segue {
    static let ACCOUNT = "accountSegue"
    static let ACCOUNT_EDIT = "accountEditSegue"
    static let SETTING = "settingSegue"
    static let PASSWORD = "passwordSegue"
    static let WEB_ABOUT = "webAboutSegue"
    static let WEB_PRIVACY_POLICY = "webPrivacyPolicySegue"
    static let WEB_TERM_OF_USE = "webTermOfUseSegue"
    static let ANIMATION = "animationSegue"
    static let UPLOAD = "uploadSegue"
    static let DEBUG = "debugSegue"
    static let RECOMMEND = "recommendSegue"
    static let FAN = "fanSegue"
    static let FOLLOW = "followSegue"
    static let BACKGROUND_COLOR = "backgroundColorSegue"
    static let WORK = "workSegue"
    static let COMMENT = "commentSegue"
    static let REPORT = "reportSegue"
    static let EDIT = "editSegue"
}

struct Default {
    static let DRAFT_BACKGROUND_COLOR = "draftBackgroundColor"
    static let GRID = "grid"
    static let COLORS = "colors"
    static let TOKEN_TIMESTAMP = "tokenTimestamp"
    static let TOKEN = "token"
    static let REMOTE_TOKEN = "remoteToken"
    static let USER_ID = "userId"
    static let USER_TYPE = "userType"
    static let USER_GROUP = "userGroup"
    static let NAME = "name"
    static let EMAIL = "email"
    static let PASSWORD = "password"
    static let IMAGE = "image"
    static let THIRD_PARTY_ID = "thirdPartyId"
    static let THIRD_PARTY_EMAIL = "thirdPartyEmail"
    static let THIRD_PARTY_TOKEN = "thirdPartyToken"
}

struct Url {
    static let PRIVACY_POLICY = "https"
}

struct NotificationName {
    static let REMOTE_TOKEN = "remoteToken"
    static let PROFILE_CHANGE = "profileChanged"
}
