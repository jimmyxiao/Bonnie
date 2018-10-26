//
//  ForgetPasswordViewController.swift
//  BonnieDraw
//
//  Created by Professor on 21/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class ForgetPasswordViewController: BackButtonViewController, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var email: UITextField!
    private var dataRequest: DataRequest?

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    private func presentErrorDialog(message: String?) {
        presentAlert(title: "alert_forget_password_title".localized, message: message)
        loading.hide(true)
    }

    @IBAction func send(_ sender: Any) {
        guard AppDelegate.reachability.connection != .none else {
            presentAlert(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        if email.isEmpty {
            presentAlert(title: "alert_forget_password_title".localized, message: "alert_sign_in_fail_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentAlert(title: "alert_forget_password_title".localized, message: "alert_sign_in_fail_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else {
            view.endEditing(true)
            loading.hide(false)
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.FORGET_PASSWORD),
                    method: .post,
                    parameters: ["email": email],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                        self.presentErrorDialog(message: "alert_network_unreachable_content".localized)
                        return
                    }
                    if response == 1 {
                        self.presentAlert(title: "alert_forget_password_title".localized, message: "alert_forget_password_content".localized) {
                            action in
                            self.navigationController?.popViewController(animated: true)
                        }
                    } else {
                        self.presentErrorDialog(message: data["msg"] as? String)
                    }
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.presentErrorDialog(message: error.localizedDescription)
                }
            }
        }
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
