//
//  LocalizedString.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 10/13/16.
//  Copyright © 2016 D-Link. All rights reserved.
//

import UIKit

extension String {
    var localized: String {
        var localizedString = NSLocalizedString(self, comment: self)
        if localizedString == self {
            let path = Bundle.main.path(forResource: "Base", ofType: "lproj")
            localizedString = NSLocalizedString(self, tableName: nil, bundle: Bundle(path: path!)!, value: self, comment: self)
        }
        return localizedString
    }

    func isValidEmail() -> Bool {
        let expression = try? NSRegularExpression(pattern: "[A-Z0-9a-z._%+]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,4}", options: .caseInsensitive)
        return expression?.firstMatch(in: self, options: [], range: NSMakeRange(0, characters.count)) != nil
    }

    func fromBase64() -> String? {
        guard let data = Data(base64Encoded: self) else {
            return nil
        }
        return String(data: data, encoding: .utf8)
    }

    func toBase64() -> String {
        return Data(self.utf8).base64EncodedString()
    }

    func MD5() -> String {
        let length = Int(CC_MD5_DIGEST_LENGTH)
        var digest = [UInt8](repeating: 0, count: length)
        if let data = data(using: String.Encoding.utf8) {
            data.withUnsafeBytes {
                body in
                CC_MD5(body, CC_LONG(data.count), &digest)
            }
        }
        return (0..<length).reduce("") {
            $0 + String(format: "%02x", digest[$1])
        }
    }
}
