//
//  ParentViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import KYDrawerController
import FacebookLogin
import TwitterKit

class ParentViewController: KYDrawerController, DrawerViewControllerDelegate, TabBarViewControllerDelegate {
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? DrawerViewController {
            controller.delegate = self
        } else if let controller = segue.destination as? TabBarViewController {
            controller.delegate = self
        }
    }

    func drawerDidTapDismiss() {
        setDrawerState(.closed, animated: true)
    }

    func drawer(didSelectRowAt indexPath: IndexPath) {
        if let item = DrawerViewController.DrawerItem(rawValue: indexPath.row) {
            switch item {
            case .popularWork:
                break
            case .newWork:
                break
            case .myWork:
                break
            case .category1:
                break
            case .category2:
                break
            case .category3:
                break
            case .account:
                break
            case .signOut:
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
        }
    }

    func tabBarDidTapMenu() {
        setDrawerState(.opened, animated: true)
    }
}
