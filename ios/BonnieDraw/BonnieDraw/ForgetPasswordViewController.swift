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
            presentDialog(title: "alert_sign_in_error_title".localized, message: "alert_sign_in_error_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentDialog(title: "alert_sign_in_error_title".localized, message: "alert_sign_in_error_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else {
        }
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
