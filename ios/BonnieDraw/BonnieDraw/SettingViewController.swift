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

class SettingViewController: BackButtonViewController, UITableViewDataSource, UITableViewDelegate, AccountEditViewControllerDelegate {
    @IBOutlet weak var tableView: UITableView!
    private var settings = [Setting(type: .profile, title: "setting_edit_profile".localized, segueId: Segue.ACCOUNT_EDIT)]
    var delegate: SettingViewControllerDelegate?

    override func viewDidLoad() {
        if let type = UserType(rawValue: UserDefaults.standard.integer(forKey: Default.USER_TYPE)),
           type == .email {
            settings.append(Setting(type: .password, title: "setting_change_password".localized, segueId: Segue.PASSWORD))
        }
        settings.append(contentsOf: [Setting(type: .language, title: "setting_language".localized, segueId: Segue.LANGUAGE),
                                     Setting(type: .description, title: "setting_about".localized, segueId: Segue.WEB_ABOUT),
                                     Setting(type: .privacyPolicy, title: "setting_privacy_policy".localized, segueId: Segue.WEB_PRIVACY_POLICY),
                                     Setting(type: .termOfUse, title: "setting_term_of_use".localized, segueId: Segue.WEB_TERM_OF_USE),
                                     Setting(type: .clearSearch, title: "setting_clear_search".localized, segueId: nil),
                                     Setting(type: .signOut, title: "menu_sign_out".localized, segueId: nil)])
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let indexPath = tableView.indexPathForSelectedRow {
            segue.destination.title = settings[indexPath.row].title
            tableView.deselectRow(at: indexPath, animated: true)
        }
        if let controller = segue.destination as? AccountEditViewController {
            controller.delegate = self
        } else if let controller = segue.destination as? WebViewController {
            if segue.identifier == Segue.WEB_ABOUT {
                controller.url = URL(string: Service.ABOUT)
            } else if segue.identifier == Segue.WEB_PRIVACY_POLICY {
                controller.url = URL(string: Service.PRIVACY_POLICY)
            } else if segue.identifier == Segue.WEB_TERM_OF_USE {
                controller.url = URL(string: Service.TERM_OF_USE)
            }
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return settings.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.BASIC, for: indexPath)
        cell.textLabel?.text = settings[indexPath.row].title
        if let item = SettingType(rawValue: indexPath.row) {
            if item == .clearSearch || item == .signOut {
                cell.accessoryType = .none
            } else {
                cell.accessoryType = .disclosureIndicator
            }
        }
        return cell
    }

    internal func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let setting = settings[indexPath.row]
        if let segueId = setting.segueId {
            performSegue(withIdentifier: segueId, sender: nil)
        } else {
            if setting.type == .clearSearch {
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
                                if let userId = TWTRTwitter.sharedInstance().sessionStore.session()?.userID {
                                    TWTRTwitter.sharedInstance().sessionStore.logOutUserID(userId)
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
                        defaults.removeObject(forKey: Default.NAME)
                        defaults.removeObject(forKey: Default.IMAGE)
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

    internal func accountEdit(profileDidChange profile: Profile) {
        delegate?.settings(profileDidChange: profile)
    }

    internal func accountEdit(imageDidChange image: UIImage) {
        delegate?.settings(imageDidChange: image)
    }

    enum SettingType: Int {
        case profile, password, language, description, privacyPolicy, termOfUse, clearSearch, signOut
    }

    struct Setting {
        let type: SettingType
        let title: String
        let segueId: String?
    }
}

protocol SettingViewControllerDelegate {
    func settings(profileDidChange profile: Profile)
    func settings(imageDidChange image: UIImage)
}
