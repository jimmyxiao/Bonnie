//
//  Constant.swift
//  iOSTemplate
//
//  Created by Professor on 4/22/16.
//  Copyright © 2016 Professor. All rights reserved.
//

import FirebaseRemoteConfig

let DEBUG = Bundle.main.infoDictionary?["Configuration"] as? String == "Debug"
let POINT_BUFFER_COUNT: UInt16 = 512
let LENGTH_SIZE: UInt16 = 20
let LENGTH_BYTE_SIZE = 2
let SERVICE_DEVICE_TYPE = 2
let TOKEN_LIFETIME: TimeInterval = Double.greatestFiniteMagnitude
let UPDATE_INTERVAL: TimeInterval = 3600
let ANIMATION_TIMER: TimeInterval = 1.0 / 60.0
let URL_APP_UPDATE = "https://www.appstore.com"

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

struct Cell {
    static let ACCOUNT_HEADER = "AccountHeaderCollectionReusableView"
    static let BASIC = "BasicTableViewCell"
    static let HOME = "HomeTableViewCell"
    static let COLOR_PICKER = "ColorPickerCollectionViewCell"
    static let ACCOUNT_GRID = "AccountGridCollectionViewCell"
    static let ACCOUNT_LIST = "AccountListCollectionViewCell"
    static let ACCOUNT_FOOTER = "AccountFooterCollectionReusableView"
    static let FOLLOW = "FollowTableViewCell"
    static let USER = "UserTableViewCell"
    static let NOTIFICATION = "NotificationTableViewCell"
    static let BRUSH_PICKER = "BrushPickerCollectionViewCell"
    static let COMMENT = "CommentTableViewCell"
    static let SWITCH = "SwitchTableViewCell"
}

struct Identifier {
    static let MAIN = "MainViewController"
    static let HOME = "HomeViewController"
    static let FOLLOW = "FollowViewController"
    static let NOTIFICATION = "NotificationViewController"
    static let REPORT = "ReportViewController"
    static let WORK = "WorkViewController"
    static let COMMENT = "CommentViewController"
    static let NAVIGATION = "NavigationViewController"
}

struct Segue {
    static let ACCOUNT = "AccountViewController"
    static let ACCOUNT_EDIT = "AccountEditViewController"
    static let SETTING = "SettingViewController"
    static let PASSWORD = "PasswordViewController"
    static let WEB_ABOUT = "WebAboutViewController"
    static let WEB_PRIVACY_POLICY = "WebPrivacyPolicyViewController"
    static let WEB_TERM_OF_USE = "WebTermOfUseViewController"
    static let ANIMATION = "CanvasAnimationViewController"
    static let UPLOAD = "UploadViewController"
    static let DEBUG = "DebugViewController"
    static let RECOMMEND = "RecommendViewController"
    static let FAN = "UserFanViewController"
    static let FOLLOW = "UserFollowViewController"
    static let COLOR_PICKER = "ColorPickerViewController"
    static let WORK = "WorkViewController"
    static let COMMENT = "CommentViewController"
    static let REPORT = "ReportViewController"
    static let EDIT = "EditViewController"
}

struct Defaults {
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

extension RemoteConfig {
    static let FORCE_UPDATE_CURRENT_VERSION = "force_update_current_version"
    static let FORCE_UPDATE_ENFORCED_VERSION = "force_update_enforce_version"
    static let FORCE_UPDATE_STORE_URL = "force_update_store_url"
}
