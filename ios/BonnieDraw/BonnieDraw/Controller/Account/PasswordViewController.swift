//
//  PasswordViewController.swift
//  BonnieDraw
//
//  Created by Professor on 23/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class PasswordViewController: BackButtonViewController, UITextFieldDelegate {
    @IBOutlet weak var done: UIBarButtonItem!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var currentPassword: UITextField!
    @IBOutlet weak var newPassword: UITextField!
    @IBOutlet weak var confirmPassword: UITextField!
    private var dataRequest: DataRequest?

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    private func presentErrorDialog(message: String?) {
        presentAlert(title: "alert_password_update_fail".localized, message: message)
        done.isEnabled = true
        loading.hide(true)
        newPassword.text = nil
        confirmPassword.text = nil
    }

    @IBAction func done(_ sender: UIBarButtonItem) {
        guard AppDelegate.reachability.connection != .none else {
            presentAlert(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        let currentPassword = self.currentPassword.text ?? ""
        let newPassword = self.newPassword.text ?? ""
        let confirmPassword = self.confirmPassword.text ?? ""
        if currentPassword.isEmpty {
            presentAlert(title: "alert_password_update_fail".localized, message: "alert_sign_in_fail_password_empty".localized) {
                action in
                self.currentPassword.becomeFirstResponder()
            }
        } else if currentPassword.count < 4 {
            presentAlert(title: "alert_password_update_fail".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.currentPassword.becomeFirstResponder()
            }
        } else if newPassword.isEmpty {
            presentAlert(title: "alert_password_update_fail".localized, message: "alert_sign_in_fail_password_empty".localized) {
                action in
                self.newPassword.becomeFirstResponder()
            }
        } else if newPassword.count < 4 {
            presentAlert(title: "alert_password_update_fail".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.newPassword.becomeFirstResponder()
            }
        } else if confirmPassword.isEmpty {
            presentAlert(title: "alert_password_update_fail".localized, message: "alert_sign_in_fail_password_empty".localized) {
                action in
                self.confirmPassword.becomeFirstResponder()
            }
        } else if confirmPassword.count < 4 {
            presentAlert(title: "alert_password_update_fail".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.confirmPassword.becomeFirstResponder()
            }
        } else {
            let currentSecurePassword = currentPassword.MD5()
            let newSecurePassword = newPassword.MD5()
            if UserDefaults.standard.string(forKey: Defaults.PASSWORD) != currentSecurePassword {
                presentAlert(title: "alert_password_update_fail".localized, message: "alert_old_password_unmatch".localized) {
                    action in
                    self.currentPassword.becomeFirstResponder()
                }
            } else if newPassword != confirmPassword {
                presentAlert(title: "alert_password_update_fail".localized, message: "alert_new_password_unmatch".localized) {
                    action in
                    self.confirmPassword.becomeFirstResponder()
                }
            } else {
                guard let token = UserDefaults.standard.string(forKey: Defaults.TOKEN) else {
                    return
                }
                sender.isEnabled = false
                view.endEditing(true)
                loading.hide(false)
                dataRequest = Alamofire.request(
                        Service.standard(withPath: Service.UPDATE_PASSWORD),
                        method: .post,
                        parameters: ["ui": UserDefaults.standard.integer(forKey: Defaults.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "oldPwd": currentSecurePassword, "newPwd": newSecurePassword],
                        encoding: JSONEncoding.default).validate().responseJSON {
                    response in
                    switch response.result {
                    case .success:
                        guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                            self.presentErrorDialog(message: "alert_network_unreachable_content".localized)
                            return
                        }
                        if response == 1 {
                            UserDefaults.standard.set(newSecurePassword, forKey: Defaults.PASSWORD)
                            self.onBackPressed(sender)
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
    }
}
