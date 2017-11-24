//
//  TabBarViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class TabBarViewController: UIViewController, UITabBarDelegate, HomeViewControllerDelegate, FollowViewControllerDelegate {
    @IBOutlet weak var tabBar: UITabBar!
    var delegate: TabBarViewControllerDelegate?
    var itemHome: (item: UITabBarItem, viewController: UIViewController?)?
    var itemFollow: (item: UITabBarItem, viewController: UIViewController?)?
    var itemNotification: (item: UITabBarItem, viewController: UIViewController?)?
    var itemAccount: (item: UITabBarItem, viewController: UIViewController?)?
    var customNavigationController: UINavigationController?

    internal func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        var controllers = [UIViewController]()
        if item == itemHome?.item {
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
                itemNotification?.viewController = controller
                controllers.append(controller)
            }
        } else if item == itemAccount?.item {
            if let controller = itemAccount?.viewController {
                controllers.append(controller)
            } else if let controller = UIStoryboard(name: "Account", bundle: nil).instantiateInitialViewController() as? AccountViewController {
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
                        itemHome = (items[i], navigationController.viewControllers.first)
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

    internal func home(enableMenuGesture enable: Bool) {
        delegate?.tabBar(enableMenuGesture: enable)
    }

    internal func followDidTapMenu() {
        delegate?.tabBarDidTapMenu()
    }

    internal func follow(enableMenuGesture enable: Bool) {
        delegate?.tabBar(enableMenuGesture: enable)
    }
}

protocol TabBarViewControllerDelegate {
    func tabBarDidTapMenu()

    func tabBar(enableMenuGesture enable: Bool)
}
