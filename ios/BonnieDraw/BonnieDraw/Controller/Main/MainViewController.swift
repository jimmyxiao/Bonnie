//
//  MainViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import KYDrawerController
import FacebookLogin
import TwitterKit

class MainViewController: KYDrawerController, DrawerViewControllerDelegate, TabBarViewControllerDelegate {
    override var prefersStatusBarHidden: Bool {
        return false
    }

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

    func drawer(didSelectType type: DrawerViewController.TagType, withTag tag: String?) {
        switch type {
        case .account:
            performSegue(withIdentifier: Segue.ACCOUNT_EDIT, sender: nil)
        case .signOut:
            presentConfirmationAlert(title: "menu_sign_out".localized, message: "alert_sign_out_content".localized) {
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
                    if let controller = UIStoryboard(name: UIDevice.current.userInterfaceIdiom == .pad ? "Login_iPad" : "Login", bundle: nil).instantiateInitialViewController() {
                        UIApplication.shared.replace(rootViewControllerWith: controller)
                    }
                }
            }
        default:
            if let tabBarController = mainViewController as? TabBarViewController,
               let controller = tabBarController.itemHome?.viewController {
                controller.setTag(type: type, tag: tag)
            }
        }
        setDrawerState(.closed, animated: true)
    }

    func tabBarDidTapMenu() {
        setDrawerState(.opened, animated: true)
    }

    func tabBar(enableMenuGesture enable: Bool) {
        screenEdgePanGestureEnabled = enable
    }
}
