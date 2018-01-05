//
//  Work.swift
//  BonnieDraw
//
//  Created by Professor on 16/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

struct Work: Comparable {
    let id: Int?
    let userId: Int?
    let profileImage: URL?
    let profileName: String?
    let thumbnail: URL?
    let file: URL?
    var title: String?
    var summery: String?
    let date: Date?
    var isFollow: Bool?
    var isLike: Bool?
    var isCollect: Bool?
    var likes: Int?
    var comments: Int?
    var accessControl: AccessControl?
    var messages = [Message]()

    init(withDictionary dictionary: [String: Any]) {
        var messageList = [Message]()
        if let messages = dictionary["msgList"] as? [[String: Any]] {
            for message in messages {
                messageList.append(Message(withDictionary: message))
            }
        }
        id = dictionary["worksId"] as? Int
        userId = dictionary["userId"] as? Int
        profileImage = URL(string: Service.filePath(withSubPath: dictionary["profilePicture"] as? String))
        profileName = dictionary["userName"] as? String
        thumbnail = URL(string: Service.filePath(withSubPath: dictionary["imagePath"] as? String))
        file = URL(string: Service.filePath(withSubPath: dictionary["bdwPath"] as? String))
        title = dictionary["title"] as? String
        summery = dictionary["description"] as? String
        if let date = dictionary["updateDate"] as? Int {
            self.date = Date(timeIntervalSince1970: Double(date) / 1000)
        } else {
            date = nil
        }
        isFollow = dictionary["isFollowing"] as? Bool
        isLike = dictionary["like"] as? Bool
        isCollect = dictionary["collection"] as? Bool
        likes = dictionary["likeCount"] as? Int
        comments = dictionary["msgCount"] as? Int
        accessControl = AccessControl(rawValue: (dictionary["privacyType"] as? Int) ?? 0)
        messages = messageList
    }

    static func <(lhs: Work, rhs: Work) -> Bool {
        if let lhsDate = lhs.date, let rhsDate = rhs.date {
            return lhsDate.compare(rhsDate) == .orderedDescending
        }
        return false
    }

    static func ==(lhs: Work, rhs: Work) -> Bool {
        return lhs.id == rhs.id
    }
}
