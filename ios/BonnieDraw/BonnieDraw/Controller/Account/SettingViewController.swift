//
//  SettingViewController.swift
//  BonnieDraw
//
//  Created by Professor on 23/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import FacebookLogin
import TwitterKit
import DeviceKit
import Alamofire

class SettingViewController: BackButtonViewController, UITableViewDataSource, UITableViewDelegate, AccountEditViewControllerDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    private var dataRequest: DataRequest?
    private var settings = [Setting(type: .notification, title: nil, segueId: nil),
                            Setting(type: .profile, title: "setting_edit_profile".localized, segueId: Segue.ACCOUNT_EDIT)]
    private var completionHandler: ((String?) -> Void)?
    var delegate: SettingViewControllerDelegate?

    override func viewDidLoad() {
        navigationItem.title = title
        if let type = UserType(rawValue: UserDefaults.standard.integer(forKey: Defaults.USER_TYPE)),
           type == .email {
            settings.append(Setting(type: .password, title: "setting_change_password".localized, segueId: Segue.PASSWORD))
        }
        settings.append(contentsOf: [Setting(type: .about, title: "setting_about".localized, segueId: Segue.WEB_ABOUT),
                                     Setting(type: .privacyPolicy, title: "setting_privacy_policy".localized, segueId: Segue.WEB_PRIVACY_POLICY),
                                     Setting(type: .termOfUse, title: "setting_term_of_use".localized, segueId: Segue.WEB_TERM_OF_USE),
                                     Setting(type: .signOut, title: "menu_sign_out".localized, segueId: nil)])
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
        NotificationCenter.default.removeObserver(self)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let indexPath = tableView.indexPathForSelectedRow {
            segue.destination.title = settings[indexPath.row].title
        }
        if let controller = segue.destination as? AccountEditViewController {
            controller.delegate = self
        } else if let controller = segue.destination as? WebViewController {
            if segue.identifier == Segue.WEB_ABOUT {
                controller.url = URL(string: Service.ABOUT + "?lang=\(Locale.current.languageCode ?? "")-\(Locale.current.regionCode ?? "")")
            } else if segue.identifier == Segue.WEB_PRIVACY_POLICY {
                controller.url = URL(string: Service.PRIVACY_POLICY + "?lang=\(Locale.current.languageCode ?? "")-\(Locale.current.regionCode ?? "")")
            } else if segue.identifier == Segue.WEB_TERM_OF_USE {
                controller.url = URL(string: Service.TERM_OF_USE + "?lang=\(Locale.current.languageCode ?? "")-\(Locale.current.regionCode ?? "")")
            }
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return settings.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let setting = settings[indexPath.row]
        if setting.type != .notification {
            let cell = tableView.dequeueReusableCell(withIdentifier: Cell.BASIC, for: indexPath)
            cell.textLabel?.text = setting.title
            if setting.type == .signOut {
                cell.accessoryType = .none
            } else {
                cell.accessoryType = .disclosureIndicator
            }
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: Cell.SWITCH, for: indexPath) as! SwitchTableViewCell
            cell.toggle.isOn = UIApplication.shared.isRegisteredForRemoteNotifications
            return cell
        }
    }

    internal func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let setting = settings[indexPath.row]
        if let segueId = setting.segueId {
            performSegue(withIdentifier: segueId, sender: nil)
        } else if setting.type != .notification {
            presentConfirmationDialog(title: "menu_sign_out".localized, message: "alert_sign_out_content".localized) {
                success in
                if success {
                    let defaults = UserDefaults.standard
                    if let type = UserType(rawValue: defaults.integer(forKey: Defaults.USER_TYPE)) {
                        switch type {
                        case .facebook:
                            LoginManager().logOut()
                            break
                        case .google:
                            GIDSignIn.sharedInstance().signOut()
                            break
                        case .twitter:
                            if let userId = TWTRTwitter.sharedInstance().sessionStore.session()?.userID {
                                TWTRTwitter.sharedInstance().sessionStore.logOutUserID(userId)
                            }
                            break
                        default:
                            break
                        }
                    }
                    defaults.removeObject(forKey: Defaults.TOKEN)
                    defaults.removeObject(forKey: Defaults.USER_ID)
                    defaults.removeObject(forKey: Defaults.USER_TYPE)
                    defaults.removeObject(forKey: Defaults.USER_GROUP)
                    defaults.removeObject(forKey: Defaults.TOKEN_TIMESTAMP)
                    defaults.removeObject(forKey: Defaults.THIRD_PARTY_TOKEN)
                    defaults.removeObject(forKey: Defaults.THIRD_PARTY_ID)
                    defaults.removeObject(forKey: Defaults.THIRD_PARTY_EMAIL)
                    defaults.removeObject(forKey: Defaults.NAME)
                    defaults.removeObject(forKey: Defaults.IMAGE)
                    UIApplication.shared.unregisterForRemoteNotifications()
                    if let controller = UIStoryboard(name: Device().isPad ? "Login_iPad" : "Login", bundle: nil).instantiateInitialViewController() {
                        UIApplication.shared.replace(rootViewControllerWith: controller)
                    }
                }
            }
        }
        tableView.deselectRow(at: indexPath, animated: true)
    }

    @IBAction func toggleNotification(_ sender: UISwitch) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        if sender.isOn {
            checkNotificationPermission(
                    successHandler: {
                        if !Device().isSimulator {
                            NotificationCenter.default.addObserver(self, selector: #selector(self.didRegisterForRemoteNotifications), name: Notification.Name(rawValue: NotificationName.REMOTE_TOKEN), object: nil)
                            UIApplication.shared.registerForRemoteNotifications()
                        } else {
                            sender.isOn = false
                        }
                    },
                    failHandler: {
                        sender.isOn = false
                    })
        } else {
            UIApplication.shared.unregisterForRemoteNotifications()
        }
    }

    @objc private func didRegisterForRemoteNotifications(notification: Notification) {
        NotificationCenter.default.removeObserver(self)
        if let token = notification.object as? String,
           let userType = UserType(rawValue: UserDefaults.standard.integer(forKey: Defaults.USER_TYPE)) {
            var postData: [String: Any] = ["ut": userType.rawValue, "dt": SERVICE_DEVICE_TYPE, "fn": 1, "token": token]
            let defaults = UserDefaults.standard
            if userType == .email {
                postData["uc"] = defaults.string(forKey: Defaults.EMAIL)
                postData["up"] = defaults.string(forKey: Defaults.PASSWORD)
            } else {
                postData["un"] = defaults.string(forKey: Defaults.NAME)
                postData["uc"] = defaults.string(forKey: Defaults.THIRD_PARTY_ID)
                postData["thirdEmail"] = defaults.string(forKey: Defaults.THIRD_PARTY_EMAIL)
            }
            if let deviceId = UIDevice.current.identifierForVendor?.uuidString {
                postData["deviceId"] = deviceId
            }
            loading.hide(false)
            dataRequest?.cancel()
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.LOGIN),
                    method: .post,
                    parameters: postData,
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                self.loading.hide(true)
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                        self.presentDialog(title: "service_download_fail_title".localized, message: "alert_network_unreachable_content".localized)
                        UIApplication.shared.unregisterForRemoteNotifications()
                        self.tableView.reloadRows(at: [IndexPath(row: SettingType.notification.rawValue, section: 0)], with: .automatic)
                        return
                    }
                    if response == 1, let token = data["lk"] as? String, let userId = data["ui"] as? Int {
                        let defaults = UserDefaults.standard
                        defaults.set(token, forKey: Defaults.TOKEN)
                        defaults.set(userId, forKey: Defaults.USER_ID)
                    } else {
                        UIApplication.shared.unregisterForRemoteNotifications()
                        self.tableView.reloadRows(at: [IndexPath(row: SettingType.notification.rawValue, section: 0)], with: .automatic)
                    }
                case .failure(let error):
                    UIApplication.shared.unregisterForRemoteNotifications()
                    self.tableView.reloadRows(at: [IndexPath(row: SettingType.notification.rawValue, section: 0)], with: .automatic)
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.presentDialog(title: "service_download_fail_title".localized, message: error.localizedDescription)
                }
            }
        } else {
            tableView.reloadRows(at: [IndexPath(row: SettingType.notification.rawValue, section: 0)], with: .automatic)
        }
    }

    internal func accountEdit(profileDidChange profile: Profile) {
        delegate?.settings(profileDidChange: profile)
    }

    internal func accountEdit(imageDidChange image: UIImage) {
        delegate?.settings(imageDidChange: image)
    }

    enum SettingType: Int {
        case notification, profile, password, about, privacyPolicy, termOfUse, signOut
    }

    struct Setting {
        let type: SettingType
        let title: String?
        let segueId: String?
    }
}

protocol SettingViewControllerDelegate {
    func settings(profileDidChange profile: Profile)
    func settings(imageDidChange image: UIImage)
}
