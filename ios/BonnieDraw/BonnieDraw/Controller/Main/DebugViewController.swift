//
//  DebugViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Crashlytics
import FacebookLogin
import TwitterKit
import DeviceKit
import Alamofire

class DebugViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    let items = ["Login", "Crash"]

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.BASIC, for: indexPath)
        cell.textLabel?.text = items[indexPath.row]
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch indexPath.row {
        case 0:
            if let controller = UIStoryboard(name: Device().isPad ? "Login_iPad" : "Login", bundle: nil).instantiateInitialViewController() {
                UIApplication.shared.replace(rootViewControllerWith: controller)
            }
//            if let userType = UserType(rawValue: UserDefaults.standard.integer(forKey: Default.USER_TYPE)) {
//                var postData: [String: Any] = ["ut": userType.rawValue, "dt": SERVICE_DEVICE_TYPE, "fn": 1]
//                let defaults = UserDefaults.standard
//                if userType == .email {
//                    postData["uc"] = defaults.string(forKey: Default.EMAIL)
//                    postData["up"] = defaults.string(forKey: Default.PASSWORD)
//                } else {
//                    postData["un"] = defaults.string(forKey: Default.NAME)
//                    postData["uc"] = defaults.string(forKey: Default.THIRD_PARTY_ID)
//                    postData["thirdEmail"] = defaults.string(forKey: Default.THIRD_PARTY_EMAIL)
//                }
//                Alamofire.request(
//                        Service.standard(withPath: Service.LOGIN),
//                        method: .post,
//                        parameters: postData,
//                        encoding: JSONEncoding.default).validate().responseJSON {
//                    response in
//                    switch response.result {
//                    case .success:
//                        guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1 else {
//                            self.presentDialog(message: "alert_network_unreachable_content".localized)
//                            return
//                        }
//                        let defaults = UserDefaults.standard
//                        if let type = UserType(rawValue: defaults.integer(forKey: Default.USER_TYPE)) {
//                            switch type {
//                            case .facebook:
//                                LoginManager().logOut()
//                                break
//                            case .google:
//                                GIDSignIn.sharedInstance().signOut()
//                                break
//                            case .twitter:
//                                if let userId = TWTRTwitter.sharedInstance().sessionStore.session()?.userID {
//                                    TWTRTwitter.sharedInstance().sessionStore.logOutUserID(userId)
//                                }
//                                break
//                            default:
//                                break
//                            }
//                        }
//                        defaults.removeObject(forKey: Default.TOKEN)
//                        defaults.removeObject(forKey: Default.USER_ID)
//                        defaults.removeObject(forKey: Default.USER_TYPE)
//                        defaults.removeObject(forKey: Default.TOKEN_TIMESTAMP)
//                        defaults.removeObject(forKey: Default.THIRD_PARTY_TOKEN)
//                        defaults.removeObject(forKey: Default.THIRD_PARTY_ID)
//                        defaults.removeObject(forKey: Default.THIRD_PARTY_EMAIL)
//                        defaults.removeObject(forKey: Default.NAME)
//                        defaults.removeObject(forKey: Default.IMAGE)
//                        UIApplication.shared.unregisterForRemoteNotifications()
//                        if let controller = UIStoryboard(name: Device().isPad ? "Login_iPad" : "Login", bundle: nil).instantiateInitialViewController() {
//                            UIApplication.shared.replace(rootViewControllerWith: controller)
//                        }
//                    case .failure(let error):
//                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
//                    }
//                }
//            }
        case 1:
            Crashlytics.sharedInstance().crash()
        default:
            break
        }
        tableView.deselectRow(at: indexPath, animated: true)
    }

    @IBAction func dismiss(_ sender: Any) {
        dismiss(animated: true)
    }
}
