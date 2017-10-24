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

class SettingViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var tableView: UITableView!

    enum SettingItem: Int {
        case profile, password, language, description, privacyPolicy, termOfUse, clearSearch, signOut
    }

    let settings = [Setting(title: "setting_edit_profile".localized, segueId: Segue.ACCOUNT_EDIT),
                    Setting(title: "setting_change_password".localized, segueId: Segue.PASSWORD),
                    Setting(title: "setting_language".localized, segueId: Segue.LANGUAGE),
                    Setting(title: "setting_description".localized, segueId: Segue.DESCRIPTION),
                    Setting(title: "setting_privacy_policy".localized, segueId: Segue.WEB),
                    Setting(title: "setting_term_of_use".localized, segueId: Segue.WEB),
                    Setting(title: "setting_clear_search".localized, segueId: nil),
                    Setting(title: "setting_sign_out".localized, segueId: nil)]

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let indexPath = tableView.indexPathForSelectedRow {
            tableView.deselectRow(at: indexPath, animated: true)
        }
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return settings.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.BASIC, for: indexPath)
        cell.textLabel?.text = settings[indexPath.row].title
        if let item = SettingItem(rawValue: indexPath.row) {
            if item == .clearSearch || item == .signOut {
                cell.accessoryType = .none
            } else {
                cell.accessoryType = .disclosureIndicator
            }
        }
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let segueId = settings[indexPath.row].segueId {
            performSegue(withIdentifier: segueId, sender: nil)
        } else if let item = SettingItem(rawValue: indexPath.row) {
            if item == .clearSearch {
            } else {
                presentConfirmationDialog(title: "menu_sign_out".localized, message: "alert_sign_out_content".localized) {
                    success in
                    if success {
                        let defaults = UserDefaults.standard
                        if let type = UserType(rawValue: defaults.integer(forKey: Default.USER_TYPE)) {
                            switch type {
                            case .facebook:
                                LoginManager().logOut()
                                break
                            case .google:
                                GIDSignIn.sharedInstance().signOut()
                                break
                            case .twitter:
                                if let userId = Twitter.sharedInstance().sessionStore.session()?.userID {
                                    Twitter.sharedInstance().sessionStore.logOutUserID(userId)
                                }
                                break
                            default:
                                break
                            }
                        }
                        defaults.removeObject(forKey: Default.TOKEN)
                        defaults.removeObject(forKey: Default.USER_ID)
                        defaults.removeObject(forKey: Default.USER_TYPE)
                        defaults.removeObject(forKey: Default.THIRD_PARTY_ID)
                        defaults.removeObject(forKey: Default.THIRD_PARTY_NAME)
                        defaults.removeObject(forKey: Default.THIRD_PARTY_IMAGE)
                        defaults.removeObject(forKey: Default.TOKEN_TIMESTAMP)
                        if let controller = UIStoryboard(name: "Login", bundle: nil).instantiateInitialViewController() {
                            UIApplication.shared.replace(rootViewControllerWith: controller)
                        }
                    }
                }
            }
            tableView.deselectRow(at: indexPath, animated: true)
        }
    }

    struct Setting {
        let title: String
        let segueId: String?
    }
}
