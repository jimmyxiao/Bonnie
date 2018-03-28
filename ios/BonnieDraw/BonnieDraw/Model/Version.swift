//
//  Version.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 3/31/17.
//  Copyright © 2017 D-Link. All rights reserved.
//

import UIKit

struct Version {
    static var current: Version? {
        if let currentString = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String,
           let buildVersion = Bundle.main.infoDictionary?["CFBundleVersion"] as? String {
            return Version(version: "\(currentString).\(buildVersion)")
        }
        return nil
    }
    var major: Int?
    var minor: Int?
    var build: Int?

    init(version: String) {
        let split = version.trimmingCharacters(in: .whitespaces).components(separatedBy: ".")
        for value in split {
            if let version = Int(value) {
                if major == nil {
                    major = version
                } else if minor == nil {
                    minor = version
                } else if build == nil {
                    build = version
                }
            }
        }
    }

    func toString() -> String {
        var versionString = ""
        if let major = major {
            versionString.append("\(major)")
        }
        if let minor = minor {
            versionString.append(".\(minor)")
        }
        if let build = build {
            versionString.append(".\(build)")
        }
        return versionString
    }
}

func >(lhs: Version, rhs: Version) -> Bool {
    if let leftMajor = lhs.major, let rightMajor = rhs.major {
        if leftMajor > rightMajor {
            return true
        } else if leftMajor == rightMajor, let leftMinor = lhs.minor, let rightMinor = rhs.minor {
            if leftMinor > rightMinor {
                return true
            } else if leftMinor == rightMinor, let leftBuild = lhs.build, let rightBuild = rhs.build {
                if leftBuild > rightBuild {
                    return true
                }
            }
        }
    }
    return false
}

func <(lhs: Version, rhs: Version) -> Bool {
    if let leftMajor = lhs.major, let rightMajor = rhs.major {
        if leftMajor < rightMajor {
            return true
        } else if leftMajor == rightMajor, let leftMinor = lhs.minor, let rightMinor = rhs.minor {
            if leftMinor < rightMinor {
                return true
            } else if leftMinor == rightMinor, let leftBuild = lhs.build, let rightBuild = rhs.build {
                if leftBuild < rightBuild {
                    return true
                }
            }
        }
    }
    return false
}
