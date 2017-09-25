//
//  SignUpViewController.swift
//  BonnieDraw
//
//  Created by Professor on 20/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SignUpViewController: BackButtonViewController, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    let client = RestClient.standard(withPath: Service.LOGIN)

    override func viewWillDisappear(_ animated: Bool) {
        client.cancel()
    }

    @IBAction func signUp(_ sender: Any) {
        guard AppDelegate.reachability.isReachable else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        let name = self.name.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let password = self.password.text ?? ""
        if name.isEmpty {
            presentDialog(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_name_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if email.isEmpty {
            presentDialog(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentDialog(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if password.isEmpty {
            presentDialog(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_password_empty".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else if password.characters.count < 4 {
            presentDialog(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else {
            loading.hide(hide: false)
            var postData: [String: Any] = ["uc": email, "up": password.MD5(), "un": name, "ut": 1, "dt": 2, "fn": 2]
            client.getResponse(queries: nil, data: postData) {
                success, data in
                guard success, let response = data?["res"] as? Int else {
                    self.presentDialog(title: "alert_sign_up_fail_title".localized, message: "app_network_unreachable_content".localized)
                    self.loading.hide(hide: true)
                    return
                }
                if response == 1 {
                    postData["fn"] = 1
                    self.client.getResponse(queries: nil, data: postData) {
                        success, data in
                        guard success, let response = data?["res"] as? Int else {
                            self.presentDialog(title: "alert_sign_up_fail_title".localized, message: data?["app_network_unreachable_content"] as? String)
                            self.loading.hide(hide: true)
                            return
                        }
                        if response == 1,
                           let token = data?["lk"] as? String, let userId = data?["ui"] as? Int {
                            UserDefaults.standard.set(token, forKey: Default.TOKEN)
                            UserDefaults.standard.set(userId, forKey: Default.USER_ID)
                            if let controller = UIStoryboard(name: "Main", bundle: nil).instantiateInitialViewController() {
                                UIApplication.shared.replace(rootViewControllerWith: controller)
                            }
                        } else {
                            self.presentDialog(title: "alert_sign_up_fail_title".localized, message: data?["msg"] as? String)
                            self.loading.hide(hide: true)
                        }
                    }
                } else {
                    self.presentDialog(title: "alert_sign_up_fail_title".localized, message: data?["msg"] as? String)
                    self.loading.hide(hide: true)
                }
            }
        }
    }

    @IBAction func popToSignIn(_ sender: Any) {
        navigationController?.popToRootViewController(animated: true)
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == name {
            email.becomeFirstResponder()
            return false
        } else if textField == email {
            password.becomeFirstResponder()
            return false
        }
        textField.resignFirstResponder()
        return true
    }
}
