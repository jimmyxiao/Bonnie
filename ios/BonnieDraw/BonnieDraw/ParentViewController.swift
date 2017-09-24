//
//  ParentViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import KYDrawerController

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
                        UserDefaults.standard.removeObject(forKey: Default.TOKEN)
                        UserDefaults.standard.removeObject(forKey: Default.USER_ID)
                        UserDefaults.standard.removeObject(forKey: Default.THIRD_PARTY_ID)
                        UserDefaults.standard.removeObject(forKey: Default.THIRD_PARTY_NAME)
                        if let controller = UIStoryboard(name: "Login", bundle: nil).instantiateInitialViewController() {
                            UIApplication.shared.replace(rootViewControllerWith: controller)
                        }
                    }
                }
            }
        }
    }

    func tabBarOpenDrawer() {
        setDrawerState(.opened, animated: true)
    }
}
