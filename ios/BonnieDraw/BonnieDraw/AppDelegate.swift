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
import FacebookCore
import FirebaseDatabase
import TwitterKit
import Reachability
import DeviceKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    static let reachability = Reachability()!
    static var pendingWorkId: Int?
    var window: UIWindow?

    internal func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        SDKApplicationDelegate.shared.application(application, didFinishLaunchingWithOptions: launchOptions)
        TWTRTwitter.sharedInstance().start(
                withConsumerKey: Bundle.main.infoDictionary?["TwitterConsumerKey"] as! String,
                consumerSecret: Bundle.main.infoDictionary?["TwitterConsumerSecret"] as! String)
        if let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") {
            let dictionary = NSDictionary(contentsOfFile: path)
            GIDSignIn.sharedInstance().clientID = dictionary?.object(forKey: "CLIENT_ID") as? String
        }
        if UserDefaults.standard.object(forKey: Defaults.COLORS) == nil {
            UserDefaults.standard.set(colors: UIColor.getDefaultColors(), forKey: Defaults.COLORS)
        }
        return true
    }

    internal func applicationDidBecomeActive(_ application: UIApplication) {
        let timestamp = UserDefaults.standard.object(forKey: Defaults.TOKEN_TIMESTAMP)
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
        NotificationCenter.default.post(name: .remoteToken, object: Messaging.messaging().fcmToken)
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

    internal func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        Logger.d("\(#function): \(error.localizedDescription)")
        NotificationCenter.default.post(name: .remoteToken, object: nil)
    }

    internal func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
        if let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
           components.host == Service.HOST,
           components.path == "\(Service.BASE)\(Service.SOCIAL_SHARE)",
           let queryItems = components.queryItems {
            for queryItem in queryItems {
                if queryItem.name == "id" {
                    if window?.rootViewController is MainViewController,
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
            didHandle = GIDSignIn.sharedInstance().handle(url, sourceApplication: options[UIApplication.OpenURLOptionsKey.sourceApplication] as? String, annotation: options[UIApplication.OpenURLOptionsKey.annotation])
        }
        return didHandle
    }

    internal func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb,
           let url = userActivity.webpageURL,
           let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
           let queryItems = components.queryItems {
            for queryItem in queryItems {
                if queryItem.name == "id" {
                    if window?.rootViewController is MainViewController,
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

