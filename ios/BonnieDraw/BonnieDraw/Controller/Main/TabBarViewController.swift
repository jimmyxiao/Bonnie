//
//  TabBarViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import DeviceKit
import FirebaseRemoteConfig

class TabBarViewController: UIViewController, UITabBarDelegate, HomeViewControllerDelegate, FollowViewControllerDelegate, NotificationViewControllerDelegate {
    @IBOutlet weak var tabBar: UITabBar!
    var delegate: TabBarViewControllerDelegate?
    var itemHome: (item: UITabBarItem, viewController: HomeViewController?)?
    var itemFollow: (item: UITabBarItem, viewController: FollowViewController?)?
    var itemNotification: (item: UITabBarItem, viewController: NotificationViewController?)?
    var itemAccount: (item: UITabBarItem, viewController: AccountViewController?)?
    var customNavigationController: UINavigationController?

    override func viewDidAppear(_ animated: Bool) {
        checkUpdate()
    }

    internal func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        var controllers = [UIViewController]()
        if item == itemHome?.item {
            if let controller = customNavigationController?.viewControllers.last as? HomeViewController {
                controller.setTag(type: .popularWork, tag: nil)
            }
            if let controller = itemHome?.viewController {
                controllers.append(controller)
            } else if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.HOME) as? HomeViewController {
                controller.delegate = self
                itemHome?.viewController = controller
                controllers.append(controller)
            }
        } else if item == itemFollow?.item {
            if let controller = itemFollow?.viewController {
                controllers.append(controller)
            } else if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.FOLLOW) as? FollowViewController {
                controller.delegate = self
                itemFollow?.viewController = controller
                controllers.append(controller)
            }
        } else if item == itemNotification?.item {
            if let controller = itemNotification?.viewController {
                controllers.append(controller)
            } else if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.NOTIFICATION) as? NotificationViewController {
                controller.delegate = self
                itemNotification?.viewController = controller
                controllers.append(controller)
            }
        } else if item == itemAccount?.item {
            if let controller = itemAccount?.viewController {
                controllers.append(controller)
            } else if let controller = UIStoryboard(name: Device().isPad ? "Account_iPad" : "Account", bundle: nil).instantiateInitialViewController() as? AccountViewController {
                itemAccount?.viewController = controller
                controllers.append(controller)
            }
        }
        customNavigationController?.setViewControllers(controllers, animated: true)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let navigationController = segue.destination as? UINavigationController,
           let rootController = navigationController.viewControllers.first as? HomeViewController {
            customNavigationController = navigationController
            rootController.delegate = self
            if let items = tabBar.items {
                for i in 0..<items.count {
                    switch i {
                    case 0:
                        tabBar.selectedItem = items[i]
                        itemHome = (items[i], navigationController.viewControllers.first as? HomeViewController)
                    case 1:
                        itemFollow = (items[i], nil)
                    case 3:
                        itemNotification = (items[i], nil)
                    case 4:
                        itemAccount = (items[i], nil)
                    default:
                        break
                    }
                }
            }
        }
    }

    internal func homeDidTapMenu() {
        delegate?.tabBarDidTapMenu()
    }

    internal func homeDidTapProfile() {
        selectAccountTab()
    }

    internal func home(enableMenuGesture enable: Bool) {
        delegate?.tabBar(enableMenuGesture: enable)
    }

    internal func followDidTapProfile() {
        selectAccountTab()
    }

    internal func notificationDidTapProfile() {
        selectAccountTab()
    }

    private func selectAccountTab() {
        if let item = itemAccount?.item {
            tabBar.selectedItem = item
            tabBar(tabBar, didSelect: item)
        }
    }

    private func checkUpdate() {
        RemoteConfig.remoteConfig().configSettings = RemoteConfigSettings(developerModeEnabled: DEBUG)
        RemoteConfig.remoteConfig().fetch(withExpirationDuration: DEBUG ? 0 : UPDATE_INTERVAL) {
            status, error in
            if status == .success {
                RemoteConfig.remoteConfig().activateFetched()
                if let current = Version.current,
                   let latestVersion = RemoteConfig.remoteConfig()[RemoteConfig.FORCE_UPDATE_CURRENT_VERSION].stringValue,
                   let enforcedVersion = RemoteConfig.remoteConfig()[RemoteConfig.FORCE_UPDATE_ENFORCED_VERSION].stringValue,
                   let updateUrlString = RemoteConfig.remoteConfig()[RemoteConfig.FORCE_UPDATE_STORE_URL].stringValue,
                   let updateUrl = URL(string: updateUrlString) {
                    if Version(version: enforcedVersion) > current {
                        let alert = UIAlertController(title: "alert_app_update_title".localized, message: "alert_app_update_content".localized, preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "alert_button_update".localized, style: .default) {
                            action in
                            if UIApplication.shared.canOpenURL(updateUrl) {
                                UIApplication.shared.open(updateUrl, options: [:]) {
                                    finised in
                                    exit(0)
                                }
                            }
                        })
                        alert.view.tintColor = UIColor.getAccentColor()
                        self.present(alert, animated: true)
                    } else if Version(version: latestVersion) > current {
                        let alert = UIAlertController(title: "alert_app_update_title".localized, message: "alert_app_update_content".localized, preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "alert_button_update".localized, style: .default) {
                            action in
                            if UIApplication.shared.canOpenURL(updateUrl) {
                                UIApplication.shared.open(updateUrl, options: [:]) {
                                    finised in
                                    exit(0)
                                }
                            }
                        })
                        alert.view.tintColor = UIColor.getAccentColor()
                        alert.addAction(UIAlertAction(title: "alert_button_cancel".localized, style: .cancel))
                        self.present(alert, animated: true)
                    }
                }
            } else {
                Logger.d("\(#function): \(error?.localizedDescription ?? "")")
            }
        }
    }
}

protocol TabBarViewControllerDelegate {
    func tabBarDidTapMenu()

    func tabBar(enableMenuGesture enable: Bool)
}
