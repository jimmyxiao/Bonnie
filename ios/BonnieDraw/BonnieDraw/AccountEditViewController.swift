//
//  AccountEditViewController.swift
//  BonnieDraw
//
//  Created by Professor on 18/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class AccountEditViewController: BackButtonViewController, UITextFieldDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    @IBOutlet weak var done: UIBarButtonItem!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var summery: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var phone: UITextField!
    @IBOutlet weak var gender: UIButton!
    private var viewOriginY: CGFloat = 0
    private var keyboardOnScreen = false
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    var delegate: AccountEditViewControllerDelegate?
    var profile: Profile?

    override func viewDidLoad() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        phone.addInputAccessoryView()
        if let url = UserDefaults.standard.url(forKey: Default.IMAGE) {
            profileImage.setImage(with: url)
        }
        setViewData()
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: .UIKeyboardDidShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide), name: .UIKeyboardDidHide, object: nil)
    }

    override func viewDidAppear(_ animated: Bool) {
        if profile == nil {
            downloadData()
        } else if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
            downloadData()
        } else {
            loading.hide(true)
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
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        loading.hide(false)
        done.isEnabled = false
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.USER_INFO_QUERY),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE],
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
                self.profile = Profile(withDictionary: data)
                self.profileImage.setImage(with: self.profile?.image)
                self.setViewData()
                self.loading.hide(true)
                self.timestamp = Date()
                UserDefaults.standard.set(self.profile?.image, forKey: Default.IMAGE)
                UserDefaults.standard.set(self.profile?.name, forKey: Default.NAME)
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
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        let defaults = UserDefaults.standard
        guard let token = defaults.string(forKey: Default.TOKEN) else {
            return
        }
        let name = profile?.name?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let summery = profile?.summery?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let email = profile?.email?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let phone = profile?.phone?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let gender = profile?.gender ?? .unspecified
        if name.isEmpty {
            presentDialog(title: "alert_account_update_fail_title".localized, message: "alert_sign_in_fail_name_empty".localized) {
                action in
                self.name.becomeFirstResponder()
            }
        } else if email.isEmpty {
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
                var postData: [String: Any] = ["ui": defaults.integer(forKey: Default.USER_ID),
                                               "lk": token,
                                               "dt": SERVICE_DEVICE_TYPE,
                                               "userName": name,
                                               "email": email,
                                               "description": summery,
                                               "phoneNo": phone,
                                               "gender": gender.rawValue]
                if let type = UserType(rawValue: defaults.integer(forKey: Default.USER_TYPE)),
                   type == .email {
                    postData["userCode"] = email
                } else {
                    postData["userCode"] = defaults.string(forKey: Default.THIRD_PARTY_ID)
                }
                sender.isEnabled = false
                loading.hide(false)
                dataRequest = Alamofire.request(
                        Service.standard(withPath: Service.USER_INFO_UPDATE),
                        method: .post,
                        parameters: postData,
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
                        defaults.set(name, forKey: Default.NAME)
                        defaults.set(email, forKey: Default.EMAIL)
                        if let profile = self.profile {
                            self.delegate?.accountEdit(profileDidChange: profile)
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
    }

    @IBAction func pickImage(_ sender: Any) {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            alert.addAction(UIAlertAction(title: "image_picker_camera".localized, style: .default) {
                action in
                self.checkCameraPermission(
                        successHandler: {
                            let controller = UIImagePickerController()
                            controller.delegate = self
                            controller.sourceType = .camera
                            self.present(controller, animated: true)
                        })
            })
        }
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            alert.addAction(UIAlertAction(title: "image_picker_album".localized, style: .default) {
                action in
                self.checkPhotosPermission(
                        successHandler: {
                            let controller = UIImagePickerController()
                            controller.delegate = self
                            self.present(controller, animated: true)
                        })
            })
        }
        alert.addAction(UIAlertAction(title: "alert_button_cancel".localized, style: .cancel))
        if let presention = alert.popoverPresentationController {
            presention.sourceView = profileImage
            presention.sourceRect = profileImage.bounds
        }
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }

    @IBAction func pickGender(_ sender: Any) {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            alert.addAction(UIAlertAction(title: "gender_male".localized, style: .default) {
                action in
                self.done.isEnabled = true
                self.profile?.gender = .male
                self.gender.setTitle(action.title, for: .normal)
            })
        }
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            alert.addAction(UIAlertAction(title: "gender_female".localized, style: .default) {
                action in
                self.done.isEnabled = true
                self.profile?.gender = .female
                self.gender.setTitle(action.title, for: .normal)
            })
        }
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            alert.addAction(UIAlertAction(title: "gender_unspecified".localized, style: .default) {
                action in
                self.done.isEnabled = true
                self.profile?.gender = .unspecified
                self.gender.setTitle(action.title, for: .normal)
            })
        }
        alert.addAction(UIAlertAction(title: "alert_button_cancel".localized, style: .cancel))
        if let presention = alert.popoverPresentationController {
            presention.sourceView = profileImage
            presention.sourceRect = profileImage.bounds
        }
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }

    internal func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String: Any]) {
        if let image = info[UIImagePickerControllerOriginalImage] as? UIImage {
            guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
                return
            }
            if let imageData = UIImageJPEGRepresentation(image, 0) {
                let userId = UserDefaults.standard.integer(forKey: Default.USER_ID)
                loading.hide(false)
                progressBar.isHidden = false
                progressBar.progress = 0
                Alamofire.upload(
                        multipartFormData: {
                            multipartFormData in
                            multipartFormData.append(imageData, withName: "file", fileName: "\(userId).jpg", mimeType: "image/jpg")
                        },
                        to: Service.standard(withPath: Service.FILE_UPLOAD) + "?ui=\(userId)&lk=\(token)&dt=\(SERVICE_DEVICE_TYPE)&fn=2&ftype=\(FileType.png.rawValue)",
                        encodingCompletion: {
                            encodingResult in
                            switch encodingResult {
                            case .success(let upload, _, _):
                                self.dataRequest = upload.uploadProgress(closure: {
                                    progress in
                                    self.progressBar.setProgress(self.progressBar.progress + Float(progress.fractionCompleted / 2), animated: true)
                                }).responseJSON(completionHandler: {
                                    response in
                                    switch response.result {
                                    case .success:
                                        guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1 else {
                                            self.showErrorMessage(message: "app_network_unreachable_content".localized)
                                            return
                                        }
                                        if let imageUrl = URL(string: Service.filePath(withSubPath: data["profilePicture"] as? String)) {
                                            UserDefaults.standard.set(imageUrl, forKey: Default.IMAGE)
                                            self.delegate?.accountEdit(imageDidChange: image)
                                            self.profileImage.image = image
                                            self.loading.hide(true)
                                        } else {
                                            self.showErrorMessage(message: data["msg"] as? String)
                                        }
                                    case .failure(let error):
                                        if let error = error as? URLError, error.code == .cancelled {
                                            return
                                        }
                                        self.showErrorMessage(message: error.localizedDescription)
                                    }
                                })
                            case .failure(let error):
                                if let error = error as? URLError, error.code == .cancelled {
                                    return
                                }
                                self.showErrorMessage(message: error.localizedDescription)
                            }
                        })
            }
        }
        picker.dismiss(animated: true)
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    @IBAction func textFieldTextDidChange(_ sender: UITextField) {
        done.isEnabled = true
        switch sender {
        case name:
            profile?.name = sender.text
        case summery:
            profile?.summery = sender.text
        case email:
            profile?.email = sender.text
        case phone:
            profile?.phone = sender.text
        default:
            return
        }
    }

    private func setViewData() {
        name.text = profile?.name
        summery.text = profile?.summery
        email.text = profile?.email
        phone.text = profile?.phone
        if let gender = profile?.gender {
            switch gender {
            case .male:
                self.gender.setTitle("gender_male".localized, for: .normal)
            case .female:
                self.gender.setTitle("gender_female".localized, for: .normal)
            case .unspecified:
                self.gender.setTitle("gender_unspecified".localized, for: .normal)
            }
        } else {
            gender.setTitle("gender_unspecified".localized, for: .normal)
        }
    }

    private func showErrorMessage(message: String?) {
        presentDialog(title: "alert_save_fail_title".localized, message: message)
        loading.hide(true)
        progressBar.isHidden = true
    }
}

protocol AccountEditViewControllerDelegate {
    func accountEdit(profileDidChange profile: Profile)
    func accountEdit(imageDidChange image: UIImage)
}
