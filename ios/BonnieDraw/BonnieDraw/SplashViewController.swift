//
//  SplashViewController.swift
//  BonnieDraw
//
//  Created by Professor on 05/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import CoreData
import Alamofire

class SplashViewController: UIViewController {
    override func viewDidAppear(_ animated: Bool) {
        if let timestamp = UserDefaults.standard.object(forKey: Default.TOKEN_TIMESTAMP) as? Date,
           let userType = UserType(rawValue: UserDefaults.standard.integer(forKey: Default.USER_TYPE)) {
            if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 >= TOKEN_LIFETIME {
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
                Alamofire.request(
                        Service.standard(withPath: Service.LOGIN),
                        method: .post,
                        parameters: postData,
                        encoding: JSONEncoding.default).validate().responseJSON {
                    response in
                    switch response.result {
                    case .success:
                        guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                            self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized) {
                                action in
                                self.launchLogin()
                            }
                            return
                        }
                        if response == 1, let token = data["lk"] as? String, let userId = data["ui"] as? Int {
                            Alamofire.request(
                                    Service.standard(withPath: Service.USER_INFO_QUERY),
                                    method: .post,
                                    parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE],
                                    encoding: JSONEncoding.default).validate().responseJSON {
                                response in
                                switch response.result {
                                case .success:
                                    guard let data = response.result.value as? [String: Any] else {
                                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized) {
                                            action in
                                            self.launchLogin()
                                        }
                                        return
                                    }
                                    Logger.d(token)
                                    let defaults = UserDefaults.standard
                                    defaults.set(token, forKey: Default.TOKEN)
                                    if let imageUrl = data["profilePicture"] as? String {
                                        defaults.set(imageUrl, forKey: Default.THIRD_PARTY_IMAGE)
                                    }
                                    defaults.set(Date(), forKey: Default.TOKEN_TIMESTAMP)
                                    self.launchMain()
                                case .failure(let error):
                                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription) {
                                        action in
                                        self.launchLogin()
                                    }
                                }
                            }
                        } else {
                            self.presentDialog(title: "alert_sign_in_fail_title".localized, message: data["msg"] as? String) {
                                action in
                                self.launchLogin()
                            }
                        }
                    case .failure(let error):
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription) {
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
