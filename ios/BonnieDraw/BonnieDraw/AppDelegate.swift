//
//  AppDelegate.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import SystemConfiguration
import Firebase
import FacebookCore
import TwitterKit
import Reachability
import Photos
import FirebaseDatabase

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    static let reachability = Reachability()!
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        SDKApplicationDelegate.shared.application(application, didFinishLaunchingWithOptions: launchOptions)
        Twitter.sharedInstance().start(
                withConsumerKey: Bundle.main.infoDictionary?["TwitterConsumerKey"] as! String,
                consumerSecret: Bundle.main.infoDictionary?["TwitterConsumerSecret"] as! String)
        if UserDefaults.standard.object(forKey: Default.COLORS) == nil {
            UserDefaults.standard.set(colors: UIColor.getDefaultColors(), forKey: Default.COLORS)
        }
        return true
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey: Any] = [:]) -> Bool {
        var didHandle = SDKApplicationDelegate.shared.application(app, open: url, options: options)
        if !didHandle {
            didHandle = Twitter.sharedInstance().application(app, open: url, options: options)
        }
        if !didHandle {
            didHandle = GIDSignIn.sharedInstance().handle(url, sourceApplication: options[UIApplicationOpenURLOptionsKey.sourceApplication] as? String, annotation: options[UIApplicationOpenURLOptionsKey.annotation])
        }
        return didHandle
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        let timestamp = UserDefaults.standard.object(forKey: Default.TOKEN_TIMESTAMP)
        if timestamp != nil, let timestamp = timestamp as? Date,
           Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 >= TOKEN_LIFETIME,
           let controller = UIStoryboard(name: "Main", bundle: nil).instantiateInitialViewController() {
            UIApplication.shared.replace(rootViewControllerWith: controller)
        }
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let token = deviceToken.map {
            String(format: "%02.2hhx", $0)
        }.joined()
        UserDefaults.standard.set(token, forKey: Default.TOKEN)
        UserDefaults.standard.set(true, forKey: Default.NOTIFICATION_ENABLED)
        NotificationCenter.default.post(name: Notification.Name(rawValue: NotificationName.REMOTE_TOKEN), object: token)
        if DEBUG {
            let deviceName = UIDevice.current.name
            let reference = Database.database().reference()
            reference.child("tokens").observeSingleEvent(of: .value, with: {
                snapshot in
                if snapshot.exists() {
                    if var tokens = snapshot.value as? [String: Any] {
                        tokens[deviceName] = ["FCMToken": Messaging.messaging().fcmToken, "APNSToken": token]
                        snapshot.ref.setValue(tokens)
                    }
                } else {
                    snapshot.ref.setValue([deviceName: ["FCMToken": Messaging.messaging().fcmToken, "APNSToken": token]])
                }
            })
        }
    }

    static func hasNetworkConnection() -> Bool {
        var zeroAddress = sockaddr_in()
        zeroAddress.sin_len = UInt8(MemoryLayout<sockaddr_in>.size)
        zeroAddress.sin_family = sa_family_t(AF_INET)
        guard let defaultRouteReachability = withUnsafePointer(to: &zeroAddress, {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
                SCNetworkReachabilityCreateWithAddress(nil, $0)
            }
        }) else {
            return false
        }
        var flags: SCNetworkReachabilityFlags = []
        if !SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags) {
            return false
        }
        let isReachable = flags.contains(.reachable)
        let needsConnection = flags.contains(.connectionRequired)
        return (isReachable && !needsConnection)
    }

    static func openSettings() {
        if let url = URL(string: UIApplicationOpenSettingsURLString), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url)
        }
    }

    static func randomColor() -> String {
        return String(format: "%02x%02x%02x", arc4random_uniform(256), arc4random_uniform(256), arc4random_uniform(256))
    }

    private static func fetchAlbum() -> PHAssetCollection? {
        if let name = Bundle.main.object(forInfoDictionaryKey: "CFBundleName") as? String {
            let collections = PHAssetCollection.fetchTopLevelUserCollections(with: nil)
            for index in 0..<collections.count {
                let collection = collections.object(at: index)
                if collection.localizedTitle == name, let collection = collection as? PHAssetCollection {
                    return collection
                }
            }
        }
        return nil
    }

    static func save(asset: UIImage, date: Date? = nil) {
        PHPhotoLibrary.shared().performChanges({
            let createAsset = PHAssetChangeRequest.creationRequestForAsset(from: asset)
            if let date = date {
                createAsset.creationDate = date
            }
            if let alblum = fetchAlbum() {
                let createAlbum = PHAssetCollectionChangeRequest(for: alblum)
                createAlbum?.addAssets(NSArray(object: createAsset.placeholderForCreatedAsset as Any))
            } else {
                if let title = Bundle.main.object(forInfoDictionaryKey: "CFBundleName") as? String {
                    let createAlbum = PHAssetCollectionChangeRequest.creationRequestForAssetCollection(withTitle: title)
                    createAlbum.addAssets(NSArray(object: createAsset.placeholderForCreatedAsset as Any))
                }
            }
        }) {
            success, error in
            if let error = error {
                Logger.p("\(#function): \(error.localizedDescription)")
            }
        }
    }
}

