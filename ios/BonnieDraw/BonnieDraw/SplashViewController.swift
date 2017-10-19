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
                        if response == 1, let token = data["lk"] as? String {
                            Alamofire.request(
                                    Service.standard(withPath: Service.CATEGORY_LIST),
                                    method: .post,
                                    parameters: ["ui": defaults.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE],
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
                                    if response == 1 {
                                        defaults.set(token, forKey: Default.TOKEN)
                                        AppDelegate.stack?.dropAllData()
                                        self.parseCategory(forData: data)
                                        self.launchMain()
                                    } else {
                                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: data["msg"] as? String) {
                                            action in
                                            self.launchLogin()
                                        }
                                    }
                                case .failure(let error):
                                    if let error = error as? URLError, error.code == .cancelled {
                                        return
                                    }
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
                        if let error = error as? URLError, error.code == .cancelled {
                            return
                        }
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

    private func parseCategory(forData data: [String: Any]?) -> Void {
        if let categories = data?["categoryList"] as? [[String: Any]] {
            AppDelegate.stack?.insertData() {
                context in
                if let description = NSEntityDescription.entity(forEntityName: "WorkCategory", in: context) {
                    for category in categories {
                        self.parseCategory(forData: category)
                        let workCategory = WorkCategory(entity: description, insertInto: context)
                        workCategory.id = (category["categoryId"] as? Int16) ?? -1
                        workCategory.name = category["categoryName"] as? String
                        workCategory.level = (category["categoryLevel"] as? Int16) ?? -1
                        var childIds = [Int16]()
                        if let childCategories = category["categoryList"] as? [[String: Any]] {
                            for childCategory in childCategories {
                                childIds.append((childCategory["categoryId"] as? Int16) ?? -1)
                            }
                        }
                        workCategory.childIds = childIds as NSObject
                    }
                }
            }
        }
    }
}
