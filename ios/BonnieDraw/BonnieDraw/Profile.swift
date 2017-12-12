//
//  Profile.swift
//  BonnieDraw
//
//  Created by Professor on 22/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

struct Profile {
    let type: UserType?
    let code: String?
    let name: String?
    let email: String?
    let worksCount: Int?
    let fansCount: Int?
    let followsCount: Int?

    init(withDictionary dictionary: [String: Any]) {
        type = UserType(rawValue: dictionary["userType"] as? Int ?? -1)
        code = dictionary["userCode"] as? String
        name = dictionary["userName"] as? String
        email = dictionary["email"] as? String
        worksCount = dictionary["worksNum"] as? Int
        fansCount = dictionary["fansNum"] as? Int
        followsCount = dictionary["followNum"] as? Int
    }
}
