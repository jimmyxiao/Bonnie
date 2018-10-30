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

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WebViewController {
            controller.url = URL(string: Service.TERM_OF_USE + "?lang=\(Locale.current.languageCode ?? "")-\(Locale.current.regionCode ?? "")")
        }
    }

    private func presentErrorMessage(message: String?) {
        presentAlert(title: "alert_sign_up_fail_title".localized, message: message)
        loading.hide(true)
        password.text = nil
    }

    @IBAction func signUp(_ sender: Any) {
        guard AppDelegate.reachability.connection != .none else {
            presentAlert(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        let name = self.name.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let password = self.password.text ?? ""
        if name.isEmpty {
            presentAlert(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_name_empty".localized) {
                action in
                self.name.becomeFirstResponder()
            }
        } else if email.isEmpty {
            presentAlert(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentAlert(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if password.isEmpty {
            presentAlert(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_password_empty".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else if password.count < 4 {
            presentAlert(title: "alert_sign_up_fail_title".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else {
            view.endEditing(true)
            loading.hide(false)
            var postData: [String: Any] = ["uc": email, "up": password.MD5(), "un": name, "ut": 1, "dt": SERVICE_DEVICE_TYPE, "fn": 2]
            let locale = Locale.current
            if let languageCode = locale.languageCode {
                postData["languageCode"] = languageCode
            }
            if let regionCode = locale.regionCode {
                postData["countryCode"] = regionCode
            }
            if let deviceId = UIDevice.current.identifierForVendor?.uuidString {
                postData["deviceId"] = deviceId
            }
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.LOGIN),
                    method: .post,
                    parameters: postData,
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                        self.presentErrorMessage(message: "alert_network_unreachable_content".localized)
                        return
                    }
                    if response == 1 {
                        self.presentAlert(title: "alert_sign_up_success_title".localized, message: "alert_sign_up_success_content".localized) {
                            action in
                            self.navigationController?.popToRootViewController(animated: true)
                        }
                    } else {
                        self.presentErrorMessage(message: data["msg"] as? String)
                    }
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.presentErrorMessage(message: error.localizedDescription)
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
