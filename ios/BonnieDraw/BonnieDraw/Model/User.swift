//
//  User.swift
//  BonnieDraw
//
//  Created by Professor on 23/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

struct User {
    let id: Int?
    let profileImage: URL?
    let profileName: String?
    var isFollowing: Bool?

    init(withDictionary dictionary: [String: Any]) {
        id = dictionary["userId"] as? Int
        profileImage = URL(string: Service.filePath(withSubPath: dictionary["profilePicture"] as? String))
        profileName = dictionary["userName"] as? String
        isFollowing = dictionary["following"] as? Bool
    }
}
