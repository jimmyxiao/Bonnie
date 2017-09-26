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
import TwitterKit

class SignInViewController: UIViewController, GIDSignInDelegate, GIDSignInUIDelegate, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var google: UIButton!
    let client = RestClient.standard(withPath: Service.LOGIN)

    override func viewDidLoad() {
        if DEBUG {
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel, target: self, action: #selector(launchMain))
        }
        GIDSignIn.sharedInstance().uiDelegate = self
        GIDSignIn.sharedInstance().delegate = self
        var configureError: NSError?
        GGLContext.sharedInstance().configureWithError(&configureError)
        if let error = configureError {
            Logger.d(error.localizedDescription)
            google.isEnabled = false
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        client.cancel()
    }

    @IBAction func signIn(_ sender: Any) {
        guard AppDelegate.reachability.isReachable else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let password = self.password.text ?? ""
        if email.isEmpty {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if password.isEmpty {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_password_empty".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else if password.characters.count < 4 {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else {
            view.endEditing(true)
            loading.hide(hide: false)
            client.components.path = Service.LOGIN
            client.getResponse(queries: nil, data: ["uc": email, "up": password.MD5(), "ut": 1, "dt": 2, "fn": 1]) {
                success, data in
                guard success, let response = data?["res"] as? Int else {
                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                    self.loading.hide(hide: true)
                    return
                }
                if response == 1, let token = data?["lk"] as? String, let userId = data?["ui"] as? Int {
                    UserDefaults.standard.set(token, forKey: Default.TOKEN)
                    UserDefaults.standard.set(userId, forKey: Default.USER_ID)
                    self.launchMain()
                } else {
                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: data?["msg"] as? String)
                    self.loading.hide(hide: true)
                    self.password.text = nil
                }
            }
        }
    }

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
                            self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                            self.loading.hide(hide: true)
                            return
                        }
                        self.checkAndLogin(withUserType: 2, userId: facebookId, name: facebookName, email: response.dictionaryValue?["email"] as? String)
                    case .failed(let error):
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
                        fallthrough
                    default:
                        self.loading.hide(hide: true)
                    }
                }
            case .failed(let error):
                self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
                fallthrough
            default:
                self.loading.hide(hide: true)
            }
        }
    }

    @IBAction func twitter(_ sender: Any) {
        loading.hide(hide: false)
        Twitter.sharedInstance().logIn() {
            session, error in
            if let error = error {
                self.loading.hide(hide: true)
                self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
            } else {
                TWTRAPIClient.withCurrentUser().requestEmail() {
                    email, error in
                    self.checkAndLogin(withUserType: 4, userId: session!.userID, name: session!.userName, email: email)
                }
            }
        }
    }

    @IBAction func google(_ sender: Any) {
        loading.hide(hide: false)
        GIDSignIn.sharedInstance().signIn()
    }

    internal func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if let error = error {
            loading.hide(hide: true)
            presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
        } else {
            checkAndLogin(withUserType: 3, userId: user.userID, name: user.profile.name, email: user.profile.email)
        }
    }

    @objc private func launchMain() {
        if let controller = UIStoryboard(name: "Main", bundle: nil).instantiateInitialViewController() {
            UIApplication.shared.replace(rootViewControllerWith: controller)
        }
    }

    private func checkAndLogin(withUserType type: Int, userId id: String, name: String, email: String?) {
        var postData: [String: Any] = ["uc": id, "un": name, "ut": type, "dt": 2, "fn": 3]
        if let email = email {
            postData["fbemail"] = email
        }
        let signInHandler: (Bool) -> Void = {
            downloadCollection in
            postData["fn"] = 1
            self.client.getResponse(queries: nil, data: postData) {
                success, data in
                guard success, let response = data?["res"] as? Int else {
                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                    self.loading.hide(hide: true)
                    return
                }
                if response == 1,
                   let token = data?["lk"] as? String, let userId = data?["ui"] as? Int {
                    UserDefaults.standard.set(token, forKey: Default.TOKEN)
                    UserDefaults.standard.set(userId, forKey: Default.USER_ID)
                    UserDefaults.standard.set(id, forKey: Default.THIRD_PARTY_ID)
                    UserDefaults.standard.set(name, forKey: Default.THIRD_PARTY_NAME)
                    self.launchMain()
                } else {
                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: data?["msg"] as? String)
                    self.loading.hide(hide: true)
                }
            }
        }
        self.client.getResponse(queries: nil, data: postData) {
            success, data in
            guard success, let response = data?["res"] as? Int else {
                self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                self.loading.hide(hide: true)
                return
            }
            if response == 1 {
                postData["fn"] = 2
                self.client.getResponse(queries: nil, data: postData) {
                    success, data in
                    guard success, let response = data?["res"] as? Int else {
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                        self.loading.hide(hide: true)
                        return
                    }
                    if response == 1 {
                        signInHandler(false)
                    } else {
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                        self.loading.hide(hide: true)
                    }
                }
            } else {
                signInHandler(true)
            }
        }
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == email {
            password.becomeFirstResponder()
            return false
        }
        textField.resignFirstResponder()
        return true
    }
}
