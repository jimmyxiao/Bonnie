//
//  Message.swift
//  BonnieDraw
//
//  Created by Professor on 08/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

struct Message {
    let id: Int?
    let userId: Int?
    let message: String?
    let date: Date?
    let userName: String?
    let userProfile: URL?

    init(withDictionary dictionary: [String: Any]) {
        var date: Date? = nil
        if let milliseconds = dictionary["creationDate"] as? Int {
            date = Date(timeIntervalSince1970: Double(milliseconds) / 1000)
        }
        id = dictionary["worksMsgId"] as? Int
        userId = dictionary["userId"] as? Int
        message = dictionary["message"] as? String
        self.date = date
        userName = dictionary["userName"] as? String
        userProfile = URL(string: Service.filePath(withSubPath: dictionary["profilePicture"] as? String))
    }
}
