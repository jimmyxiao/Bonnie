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

class DebugViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    let items = ["Sign out", "Crash"]

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
            defaults.removeObject(forKey: Default.NAME)
            defaults.removeObject(forKey: Default.IMAGE)
            defaults.removeObject(forKey: Default.TOKEN_TIMESTAMP)
            if let controller = UIStoryboard(name: "Login", bundle: nil).instantiateInitialViewController() {
                UIApplication.shared.replace(rootViewControllerWith: controller)
            }
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
