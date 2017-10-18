//
//  AppDelegate.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import SystemConfiguration
import FacebookCore
import TwitterKit
import Reachability

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    static let stack = CoreDataStack(modelName: "Model")
    static let reachability = Reachability()!
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]? = nil) -> Bool {
        SDKApplicationDelegate.shared.application(application, didFinishLaunchingWithOptions: launchOptions)
        Twitter.sharedInstance().start(
                withConsumerKey: Bundle.main.infoDictionary?["TwitterConsumerKey"] as! String,
                consumerSecret: Bundle.main.infoDictionary?["TwitterConsumerSecret"] as! String)
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
}

