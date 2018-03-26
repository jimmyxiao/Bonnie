//
//  Profile.swift
//  BonnieDraw
//
//  Created by Professor on 22/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

struct Profile: Comparable {
    let type: UserType?
    let group: UserGroup?
    var name: String?
    var email: String?
    var website: String?
    var description: String?
    var phone: String?
    var gender: Gender?
    var worksCount: Int?
    let fansCount: Int?
    var followsCount: Int?
    var isFollowing: Bool?
    var image: URL?

    init(withDictionary dictionary: [String: Any]) {
        type = UserType(rawValue: dictionary["userType"] as? Int ?? 0)
        group = UserGroup(rawValue: dictionary["userGroup"] as? Int ?? 0)
        name = dictionary["userName"] as? String
        email = dictionary["email"] as? String
        website = dictionary["webLink"] as? String
        description = dictionary["description"] as? String
        phone = dictionary["phoneNo"] as? String
        gender = Gender(rawValue: dictionary["gender"] as? Int ?? 3)
        worksCount = dictionary["worksNum"] as? Int
        fansCount = dictionary["fansNum"] as? Int
        followsCount = dictionary["followNum"] as? Int
        isFollowing = dictionary["follow"] as? Bool
        image = URL(string: Service.filePath(withSubPath: dictionary["profilePicture"] as? String))
    }

    static func <(lhs: Profile, rhs: Profile) -> Bool {
        return false
    }

    static func ==(lhs: Profile, rhs: Profile) -> Bool {
        return lhs.type == rhs.type &&
                lhs.group == rhs.group &&
                lhs.name == rhs.name &&
                lhs.email == rhs.email &&
                lhs.website == rhs.website &&
                lhs.description == rhs.description &&
                lhs.phone == rhs.phone &&
                lhs.gender == rhs.gender &&
                lhs.worksCount == rhs.worksCount &&
                lhs.fansCount == rhs.fansCount &&
                lhs.followsCount == rhs.followsCount &&
                lhs.isFollowing == rhs.isFollowing &&
                lhs.image == rhs.image
    }
}
