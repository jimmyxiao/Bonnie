//
//  SignInViewController.swift
//  BonnieDraw
//
//  Created by Professor on 15/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import FacebookCore
import FacebookLogin

class SignInViewController: UIViewController {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!

    @IBAction func facebook(_ sender: Any) {
        loading.hide(hide: false)
        LoginManager().logIn([.publicProfile, .email]) {
            result in
            switch result {
            case .success(_, _, _):
                GraphRequest(graphPath: "me", parameters: ["fields": "name,email"]).start() {
                    response, result in
                    switch result {
                    case .success(let response):
                        guard let facebookId = response.dictionaryValue?["id"] as? String,
                              let facebookName = response.dictionaryValue?["name"] as? String else {
                            self.presentDialog(title: "alert_sign_in_error_title".localized, message: "app_network_unreachable_content".localized)
                            self.loading.hide(hide: true)
                            return
                        }
                        var postData: [String: Any] = ["uc": facebookId, "ut": 2, "dt": 4, "fn": 3]
                        if let email = response.dictionaryValue?["email"] as? String {
                            postData["fbemail"] = email
                        }
                        if let controller = self.storyboard?.instantiateViewController(withIdentifier: "rootViewController") {
                            self.navigationController?.setViewControllers([controller], animated: true)
                        }
                    case .failed(let error):
                        self.presentDialog(title: "alert_sign_in_error_title".localized, message: error.localizedDescription)
                        fallthrough
                    default:
                        self.loading.hide(hide: true)
                    }
                }
            case .failed(let error):
                self.presentDialog(title: "alert_sign_in_error_title".localized, message: error.localizedDescription)
                fallthrough
            default:
                self.loading.hide(hide: true)
            }
        }
    }
}
