//
//  Work.swift
//  BonnieDraw
//
//  Created by Professor on 16/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

struct Work {
    let id: Int?
    let profileImage: URL?
    let profileName: String?
    let thumbnail: URL?
    let file: URL?
    let title: String?
    let description: String?
    var isLike: Bool?
    var isCollect: Bool?
    var likes: Int?
    var messages = [Message]()

    init(withDictionary dictionary: [String: Any]) {
        var messageList = [Message]()
        if let messages = dictionary["msgList"] as? [[String: Any]] {
            for message in messages {
                messageList.append(Message(withDictionary: message))
            }
        }
        id = dictionary["worksId"] as? Int
        profileImage = URL(string: Service.filePath(withSubPath: dictionary["profilePicture"] as? String))
        profileName = dictionary["userName"] as? String
        thumbnail = URL(string: Service.filePath(withSubPath: dictionary["imagePath"] as? String))
        file = URL(string: Service.filePath(withSubPath: dictionary["bdwPath"] as? String))
        title = dictionary["title"] as? String
        description = dictionary["description"] as? String
        isLike = dictionary["like"] as? Bool
        isCollect = dictionary["collection"] as? Bool
        likes = dictionary["likeCount"] as? Int
        messages = messageList
    }
}
