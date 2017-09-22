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

    @IBAction func signUp(_ sender: Any) {
        guard AppDelegate.reachability.isReachable else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        let name = self.name.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let password = self.password.text ?? ""
        if name.isEmpty {
            presentDialog(title: "alert_sign_in_error_title".localized, message: "alert_sign_in_error_name_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if email.isEmpty {
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
        }
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
