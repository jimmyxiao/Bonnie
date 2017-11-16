//
//  AccountEditViewController.swift
//  BonnieDraw
//
//  Created by Professor on 18/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class AccountEditViewController: BackButtonViewController, UITextFieldDelegate {
    @IBOutlet weak var done: UIBarButtonItem!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var image: UIImageView!
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var summery: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var phone: UITextField!
    @IBOutlet weak var gender: UITextField!
    private var viewOriginY: CGFloat = 0
    private var keyboardOnScreen = false
    private var dataRequest: DataRequest?
    private var timestamp: Date?

    override func viewDidLoad() {
        image.sd_setShowActivityIndicatorView(true)
        image.sd_setIndicatorStyle(.gray)
        phone.addInputAccessoryView()
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: .UIKeyboardDidShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide), name: .UIKeyboardDidHide, object: nil)
    }

    override func viewDidAppear(_ animated: Bool) {
        if let timestamp = timestamp {
            if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
                downloadData()
            }
        } else {
            downloadData()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if !keyboardOnScreen, let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size {
            viewOriginY = view.frame.origin.y
            view.frame.origin.y -= keyboardSize.height / 2
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if keyboardOnScreen {
            view.frame.origin.y = viewOriginY
        }
    }

    @objc func keyboardDidShow(_ notification: Notification) {
        keyboardOnScreen = true
    }

    @objc func keyboardDidHide(_ notification: Notification) {
        keyboardOnScreen = false
    }

    private func downloadData() {
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        guard let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
              let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        loading.hide(false)
        done.isEnabled = false
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.USER_INFO_QUERY),
                method: .post,
                parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1 else {
                    self.presentConfirmationDialog(
                            title: "service_download_fail_title".localized,
                            message: "app_network_unreachable_content".localized) {
                        success in
                        if success {
                            self.downloadData()
                        }
                    }
                    return
                }
                if let url = URL(string: Service.filePath(withSubPath: data["profilePicture"] as? String)) {
                    self.image.setImage(with: url)
                    UserDefaults.standard.set(url, forKey: Default.THIRD_PARTY_IMAGE)
                }
                self.name.text = data["userName"] as? String
                self.username.text = data["userCode"] as? String
                self.summery.text = data["description"] as? String
                self.email.text = data["email"] as? String
                self.phone.text = data["phoneNo"] as? String
                self.gender.text = data["gender"] as? String
                self.loading.hide(true)
                self.timestamp = Date()
                self.done.isEnabled = true
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.presentConfirmationDialog(
                        title: "service_download_fail_title".localized,
                        message: error.localizedDescription) {
                    success in
                    if success {
                        self.downloadData()
                    }
                }
            }
        }
    }

    @IBAction func done(_ sender: UIBarButtonItem) {
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.done(sender)
                }
            }
            return
        }
        let name = self.name.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let username = self.username.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let summery = self.summery.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let phone = self.phone.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let gender = self.gender.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        if email.isEmpty {
            presentDialog(title: "alert_account_update_fail_title".localized, message: "alert_sign_in_fail_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentDialog(title: "alert_account_update_fail_title".localized, message: "alert_sign_in_fail_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else {
            guard let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
                  let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
                return
            }
            sender.isEnabled = false
            loading.hide(false)
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.USER_INFO_UPDATE),
                    method: .post,
                    parameters: ["ui": userId,
                                 "lk": token,
                                 "dt": SERVICE_DEVICE_TYPE],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1 else {
                        self.presentConfirmationDialog(
                                title: "alert_account_update_fail_title".localized,
                                message: "app_network_unreachable_content".localized) {
                            success in
                            if success {
                                self.done(sender)
                            } else {
                                sender.isEnabled = true
                                self.loading.hide(true)
                            }
                        }
                        return
                    }
                    self.onBackPressed(sender)
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.presentConfirmationDialog(
                            title: "alert_account_update_fail_title".localized,
                            message: error.localizedDescription) {
                        success in
                        if success {
                            self.done(sender)
                        } else {
                            sender.isEnabled = true
                            self.loading.hide(true)
                        }
                    }
                }
            }
        }
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
