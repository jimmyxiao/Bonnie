//
//  SplashViewController.swift
//  BonnieDraw
//
//  Created by Professor on 05/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SplashViewController: UIViewController {
    override func viewDidAppear(_ animated: Bool) {
        if let timestamp = UserDefaults.standard.object(forKey: Default.TOKEN_TIMESTAMP) as? Date,
           let userType = UserType(rawValue: UserDefaults.standard.integer(forKey: Default.USER_TYPE)) {
            if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 >= 86400 {
                var postData: [String: Any] = ["ut": userType.rawValue, "dt": SERVICE_DEVICE_TYPE, "fn": 1]
                let defaults = UserDefaults.standard
                if userType == .email {
                    postData["uc"] = defaults.string(forKey: Default.EMAIL)
                    postData["up"] = defaults.string(forKey: Default.PASSWORD)
                } else {
                    postData["uc"] = defaults.string(forKey: Default.THIRD_PARTY_ID)
                    postData["un"] = defaults.string(forKey: Default.THIRD_PARTY_NAME)
                    postData["thirdEmail"] = defaults.string(forKey: Default.THIRD_PARTY_EMAIL)
                }
                let client = RestClient.standard(withPath: Service.LOGIN)
                client.getResponse(data: postData) {
                    success, data in
                    guard success, let response = data?["res"] as? Int else {
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized) {
                            action in
                            self.launchLogin()
                        }
                        return
                    }
                    if response == 1, let token = data?["lk"] as? String {
                        client.components.path = Service.CATEGORY_LIST
                        client.getResponse(data: ["ui": defaults.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE]) {
                            success, data in
                            if success {
                                defaults.set(token, forKey: Default.TOKEN)
                                self.launchMain()
                            } else {
                                self.presentDialog(title: "alert_sign_in_fail_title".localized, message: data?["msg"] as? String) {
                                    action in
                                    self.launchLogin()
                                }
                            }
                        }
                    } else {
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: data?["msg"] as? String) {
                            action in
                            self.launchLogin()
                        }
                    }
                }
            } else {
                launchMain()
            }
        } else {
            launchLogin()
        }
    }

    func launchMain() {
        UIApplication.shared.replace(rootViewControllerWith: UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: Identifier.PARENT))
    }

    func launchLogin() {
        if let controller = UIStoryboard(name: "Login", bundle: nil).instantiateInitialViewController() {
            UIApplication.shared.replace(rootViewControllerWith: controller)
        }
    }
}
