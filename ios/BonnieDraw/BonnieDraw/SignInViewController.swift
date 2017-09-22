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

class SignInViewController: UIViewController, GIDSignInDelegate, GIDSignInUIDelegate, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var google: UIButton!
    let client = RestClient.standard(withPath: Service.LOGIN)

    override func viewDidLoad() {
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
            presentDialog(title: "alert_sign_in_error_title".localized, message: "alert_sign_in_error_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentDialog(title: "alert_sign_in_error_title".localized, message: "alert_sign_in_error_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if password.isEmpty {
            presentDialog(title: "alert_sign_in_error_title".localized, message: "alert_sign_in_error_password_empty".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else if password.characters.count < 4 {
            presentDialog(title: "alert_sign_in_error_title".localized, message: "alert_sign_in_error_password_invalid".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else {
            loading.hide(hide: false)
            client.components.path = Service.LOGIN
            client.getResponse(queries: nil, data: ["uc": email, "up": password.MD5(), "ut": 1, "dt": 4, "fn": 1]) {
                success, data in
                guard success, let response = data?["res"] as? Int else {
                    self.presentDialog(title: "alert_sign_in_error_title".localized, message: "app_network_unreachable_content".localized)
                    self.loading.hide(hide: true)
                    return
                }
                if response == 1, let token = data?["lk"] as? String, let userId = data?["ui"] as? Int {
                    UserDefaults.standard.set(token, forKey: Default.TOKEN)
                    UserDefaults.standard.set(userId, forKey: Default.USER_ID)
                    self.launchMain()
                } else {
                    self.presentDialog(title: "alert_sign_in_error_title".localized, message: data?["msg"] as? String)
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
                            self.presentDialog(title: "alert_sign_in_error_title".localized, message: "app_network_unreachable_content".localized)
                            self.loading.hide(hide: true)
                            return
                        }
                        var postData: [String: Any] = ["uc": facebookId, "ut": 2, "dt": 4, "fn": 3]
                        if let email = response.dictionaryValue?["email"] as? String {
                            postData["fbemail"] = email
                        }
                        let facebookSignInHandler: (Bool) -> Void = {
                            downloadCollection in
                            postData["fn"] = 1
                            self.client.getResponse(queries: nil, data: postData) {
                                success, data in
                                guard success, let response = data?["res"] as? Int else {
                                    self.presentDialog(title: "alert_sign_in_error_title".localized, message: "app_network_unreachable_content".localized)
                                    self.loading.hide(hide: true)
                                    return
                                }
                                if response == 1,
                                   let token = data?["lk"] as? String, let userId = data?["ui"] as? Int {
                                    UserDefaults.standard.set(token, forKey: Default.TOKEN)
                                    UserDefaults.standard.set(userId, forKey: Default.USER_ID)
                                    UserDefaults.standard.set(facebookId, forKey: Default.FACEBOOK_ID)
                                    UserDefaults.standard.set(facebookName, forKey: Default.FACEBOOK_NAME)
                                    self.launchMain()
                                } else {
                                    self.presentDialog(title: "alert_sign_in_error_title".localized, message: data?["msg"] as? String)
                                    self.loading.hide(hide: true)
                                }
                            }
                        }
                        self.client.getResponse(queries: nil, data: postData) {
                            success, data in
                            guard success, let response = data?["res"] as? Int else {
                                self.presentDialog(title: "alert_sign_in_error_title".localized, message: "app_network_unreachable_content".localized)
                                self.loading.hide(hide: true)
                                return
                            }
                            if response == 1 {
                                postData["fn"] = 2
                                self.client.getResponse(queries: nil, data: postData) {
                                    success, data in
                                    guard success, let response = data?["res"] as? Int else {
                                        self.presentDialog(title: "alert_sign_in_error_title".localized, message: "app_network_unreachable_content".localized)
                                        self.loading.hide(hide: true)
                                        return
                                    }
                                    if response == 1 {
                                        facebookSignInHandler(false)
                                    } else {
                                        self.presentDialog(title: "alert_sign_in_error_title".localized, message: "app_network_unreachable_content".localized)
                                        self.loading.hide(hide: true)
                                    }
                                }
                            } else {
                                facebookSignInHandler(true)
                            }
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

    @IBAction func twitter(_ sender: Any) {
    }

    @IBAction func google(_ sender: Any) {
        loading.hide(hide: false)
        GIDSignIn.sharedInstance().signIn()
    }

    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if let error = error {
            presentDialog(title: "alert_sign_in_error_title".localized, message: error.localizedDescription)
        } else {
            launchMain()
        }
    }

    func launchMain() {
        if let controller = UIStoryboard(name: "Main", bundle: nil).instantiateInitialViewController() {
            UIApplication.shared.replace(rootViewControllerWith: controller)
        }
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == email {
            password.becomeFirstResponder()
            return false
        }
        textField.resignFirstResponder()
        return true
    }
}
