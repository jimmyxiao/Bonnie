//
//  Profile.swift
//  BonnieDraw
//
//  Created by Professor on 22/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class Profile: Comparable {
    let type: UserType?
    var name: String?
    var email: String?
    var description: String?
    var phone: String?
    var gender: Gender?
    let worksCount: Int?
    let fansCount: Int?
    var followsCount: Int?
    let image: URL?

    init(withDictionary dictionary: [String: Any]) {
        type = UserType(rawValue: dictionary["userType"] as? Int ?? -1)
        name = dictionary["userName"] as? String
        email = dictionary["email"] as? String
        description = dictionary["description"] as? String
        phone = dictionary["phoneNo"] as? String
        gender = Gender(rawValue: dictionary["gender"] as? Int ?? 3)
        worksCount = dictionary["worksNum"] as? Int
        fansCount = dictionary["fansNum"] as? Int
        followsCount = dictionary["followNum"] as? Int
        image = URL(string: Service.filePath(withSubPath: dictionary["profilePicture"] as? String))
    }

    static func <(lhs: Profile, rhs: Profile) -> Bool {
        return false
    }

    static func ==(lhs: Profile, rhs: Profile) -> Bool {
        return lhs.name == rhs.name &&
                lhs.description == rhs.description &&
                lhs.email == rhs.email &&
                lhs.phone == rhs.phone &&
                lhs.gender == rhs.gender
    }
}
