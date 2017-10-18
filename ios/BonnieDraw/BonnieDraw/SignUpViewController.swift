//
//  SignUpViewController.swift
//  BonnieDraw
//
//  Created by Professor on 20/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class SignUpViewController: BackButtonViewController, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    private var dataRequest: DataRequest?

    override func viewDidLoad() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "top_bar_ic_back"), style: .plain, target: self, action: #selector(onBackPressed))
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    private func showErrorMessage(message: String?) {
        presentDialog(title: "alert_sign_up_fail_title".localized, message: message)
        loading.hide(true)
        password.text = nil
    }

    @IBAction func signUp(_ sender: Any) {
        guard AppDelegate.reachability.connection != .none else {
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
            view.endEditing(true)
            loading.hide(false)
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.LOGIN),
                    method: .post,
                    parameters: ["uc": email, "up": password.MD5(), "un": name, "ut": 1, "dt": SERVICE_DEVICE_TYPE, "fn": 2],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                        self.showErrorMessage(message: "app_network_unreachable_content".localized)
                        return
                    }
                    if response == 1 {
                        self.presentDialog(title: "alert_sign_up_success_title".localized, message: "alert_sign_up_success_content".localized) {
                            action in
                            self.navigationController?.popToRootViewController(animated: true)
                        }
                    } else {
                        self.showErrorMessage(message: data["msg"] as? String)
                    }
                case .failure(let error):
                    self.showErrorMessage(message: error.localizedDescription)
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
