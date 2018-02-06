//
//  AppDelegate.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import SystemConfiguration
import Photos
import Firebase
import FirebaseDatabase
import FacebookCore
import TwitterKit
import Reachability
import DeviceKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    static let reachability = Reachability()!
    static var pendingWorkId: Int?
    var window: UIWindow?

    internal func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        SDKApplicationDelegate.shared.application(application, didFinishLaunchingWithOptions: launchOptions)
        TWTRTwitter.sharedInstance().start(
                withConsumerKey: Bundle.main.infoDictionary?["TwitterConsumerKey"] as! String,
                consumerSecret: Bundle.main.infoDictionary?["TwitterConsumerSecret"] as! String)
        if UserDefaults.standard.object(forKey: Default.COLORS) == nil {
            UserDefaults.standard.set(colors: UIColor.getDefaultColors(), forKey: Default.COLORS)
        }
        return true
    }

    internal func applicationDidBecomeActive(_ application: UIApplication) {
        let timestamp = UserDefaults.standard.object(forKey: Default.TOKEN_TIMESTAMP)
        if timestamp != nil, let timestamp = timestamp as? Date,
           Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 >= TOKEN_LIFETIME,
           let controller = UIStoryboard(name: Device().isPad ? "Main_iPad" : "Main", bundle: nil).instantiateInitialViewController() {
            UIApplication.shared.replace(rootViewControllerWith: controller)
        }
    }

    internal func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let token = deviceToken.map {
            String(format: "%02.2hhx", $0)
        }.joined()
        NotificationCenter.default.post(name: Notification.Name(rawValue: NotificationName.REMOTE_TOKEN), object: Messaging.messaging().fcmToken)
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

    internal func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        Logger.d("\(#function): \(userInfo)")
    }

    internal func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        Logger.d("\(#function): \(error.localizedDescription)")
        NotificationCenter.default.post(name: Notification.Name(rawValue: NotificationName.REMOTE_TOKEN), object: nil)
    }

    internal func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey: Any] = [:]) -> Bool {
        if let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
           components.host == Service.HOST,
           components.path == "\(Service.BASE)\(Service.SOCIAL_SHARE)",
           let queryItems = components.queryItems {
            for queryItem in queryItems {
                if queryItem.name == "id" {
                    if window?.rootViewController is ParentViewController,
                       let topController = window?.topController() {
                        let storyboard = UIStoryboard(name: Device().isPad ? "Main_iPad" : "Main", bundle: nil)
                        if let navigationController = storyboard.instantiateViewController(withIdentifier: Identifier.NAVIGATION) as? UINavigationController,
                           let controller = storyboard.instantiateViewController(withIdentifier: Identifier.WORK) as? WorkViewController {
                            controller.workId = Int(queryItem.value ?? "")
                            navigationController.setViewControllers([controller], animated: false)
                            topController.present(navigationController, animated: true)
                            return true
                        }
                    } else {
                        AppDelegate.pendingWorkId = Int(queryItem.value ?? "")
                    }
                }
            }
        }
        var didHandle = SDKApplicationDelegate.shared.application(app, open: url, options: options)
        if !didHandle {
            didHandle = TWTRTwitter.sharedInstance().application(app, open: url, options: options)
        }
        if !didHandle {
            didHandle = GIDSignIn.sharedInstance().handle(url, sourceApplication: options[UIApplicationOpenURLOptionsKey.sourceApplication] as? String, annotation: options[UIApplicationOpenURLOptionsKey.annotation])
        }
        return didHandle
    }

    internal func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([Any]?) -> Void) -> Bool {
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb,
           let url = userActivity.webpageURL,
           let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
           let queryItems = components.queryItems {
            for queryItem in queryItems {
                if queryItem.name == "id" {
                    if window?.rootViewController is ParentViewController,
                       let topController = window?.topController() {
                        let storyboard = UIStoryboard(name: Device().isPad ? "Main_iPad" : "Main", bundle: nil)
                        if let navigationController = storyboard.instantiateViewController(withIdentifier: Identifier.NAVIGATION) as? UINavigationController,
                           let controller = storyboard.instantiateViewController(withIdentifier: Identifier.WORK) as? WorkViewController {
                            controller.workId = Int(queryItem.value ?? "")
                            navigationController.setViewControllers([controller], animated: false)
                            topController.present(navigationController, animated: true)
                            return true
                        }
                    } else {
                        AppDelegate.pendingWorkId = Int(queryItem.value ?? "")
                    }
                }
            }
        }
        return false
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

