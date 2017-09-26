//
//  ForgetPasswordViewController.swift
//  BonnieDraw
//
//  Created by Professor on 21/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class ForgetPasswordViewController: UIViewController, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var email: UITextField!
    let client = RestClient.standard(withPath: Service.FORGET_PASSWORD)

    override func viewWillDisappear(_ animated: Bool) {
        client.cancel()
    }

    @IBAction func send(_ sender: Any) {
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
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
        } else {
            view.endEditing(true)
            loading.hide(hide: false)
            client.getResponse(queries: nil, data: ["email": email]) {
                success, data in
                self.loading.hide(hide: true)
                guard success, let response = data?["res"] as? Int else {
                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                    return
                }
                if response == 1 {
                    self.presentDialog(title: "alert_forget_password_title".localized, message: "alert_forget_password_content".localized) {
                        action in
                        self.navigationController?.popViewController(animated: true)
                    }
                } else {
                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: data?["msg"] as? String)
                }
            }
        }
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
