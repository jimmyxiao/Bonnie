//
//  TabBarViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class TabBarViewController: UIViewController, UITabBarDelegate, HomeViewControllerDelegate, FollowViewControllerDelegate, NotificationViewControllerDelegate {
    @IBOutlet weak var tabBar: UITabBar!
    var delegate: TabBarViewControllerDelegate?
    var itemHome: (item: UITabBarItem, viewController: HomeViewController?)?
    var itemFollow: (item: UITabBarItem, viewController: FollowViewController?)?
    var itemNotification: (item: UITabBarItem, viewController: NotificationViewController?)?
    var itemAccount: (item: UITabBarItem, viewController: AccountViewController?)?
    var customNavigationController: UINavigationController?

    override func viewWillAppear(_ animated: Bool) {
        tabBar.invalidateIntrinsicContentSize()
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
            } else if let controller = UIStoryboard(name: UIDevice.current.userInterfaceIdiom == .pad ? "Account_iPad" : "Account", bundle: nil).instantiateInitialViewController() as? AccountViewController {
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
}

protocol TabBarViewControllerDelegate {
    func tabBarDidTapMenu()

    func tabBar(enableMenuGesture enable: Bool)
}
