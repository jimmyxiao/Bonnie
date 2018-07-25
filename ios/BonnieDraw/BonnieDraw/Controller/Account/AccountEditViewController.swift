//
//  AccountEditViewController.swift
//  BonnieDraw
//
//  Created by Professor on 18/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class AccountEditViewController: BackButtonViewController, UITextFieldDelegate, UITextViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    @IBOutlet weak var done: UIBarButtonItem!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var summery: UITextView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var websiteLabel: UILabel!
    @IBOutlet weak var website: UITextField!
    @IBOutlet weak var websiteDivider: UIView!
    @IBOutlet weak var phone: UITextField!
    @IBOutlet weak var gender: UIButton!
    private var viewOriginCenterY: CGFloat?
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private var oldProfile: Profile?
    var profile: Profile?
    var delegate: AccountEditViewControllerDelegate?

    override func viewDidLoad() {
        oldProfile = profile
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        phone.addInputAccessoryView()
        if let url = UserDefaults.standard.url(forKey: Defaults.IMAGE) {
            profileImage.setImage(with: url)
        } else {
            profileImage.setImage(with: profile?.image, placeholderImage: UIImage(named: "photo-square"))
        }
        setViewData()
        if UserDefaults.standard.integer(forKey: Defaults.USER_GROUP) == 1 {
            websiteLabel.isHidden = false
            website.isHidden = false
            websiteDivider.isHidden = false
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
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
        view.endEditing(true)
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    private func presentErrorDialog(message: String?) {
        presentDialog(title: "alert_account_update_fail_title".localized, message: message)
        done.isEnabled = true
        loading.hide(true)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size,
           let animationDuration = notification.userInfo?[UIKeyboardAnimationDurationUserInfoKey] as? NSNumber,
           let animationCurve = notification.userInfo?[UIKeyboardAnimationCurveUserInfoKey] as? NSNumber {
            if viewOriginCenterY == nil {
                viewOriginCenterY = view.center.y
            }
            UIView.animate(withDuration: animationDuration.doubleValue, delay: 0, options: [UIViewAnimationOptions(rawValue: UInt(animationCurve.intValue))], animations: {
                if let viewOriginCenterY = self.viewOriginCenterY {
                    self.view.center.y = viewOriginCenterY - keyboardSize.height / 2
                }
            })
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if let animationDuration = notification.userInfo?[UIKeyboardAnimationDurationUserInfoKey] as? NSNumber,
           let animationCurve = notification.userInfo?[UIKeyboardAnimationCurveUserInfoKey] as? NSNumber {
            UIView.animate(withDuration: animationDuration.doubleValue, delay: 0, options: [UIViewAnimationOptions(rawValue: UInt(animationCurve.intValue))], animations: {
                if let viewOriginCenterY = self.viewOriginCenterY {
                    self.view.center.y = viewOriginCenterY
                }
            })
        }
    }

    private func downloadData() {
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Defaults.TOKEN) else {
            return
        }
        loading.hide(false)
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.USER_INFO_QUERY),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Defaults.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1 else {
                    self.presentConfirmationDialog(
                            title: "service_download_fail_title".localized,
                            message: "alert_network_unreachable_content".localized) {
                        success in
                        if success {
                            self.downloadData()
                        }
                    }
                    return
                }
                self.profile = Profile(withDictionary: data)
                self.profileImage.setImage(with: self.profile?.image, placeholderImage: UIImage(named: "photo-square"))
                self.setViewData()
                self.loading.hide(true)
                self.timestamp = Date()
                UserDefaults.standard.set(self.profile?.image, forKey: Defaults.IMAGE)
                UserDefaults.standard.set(self.profile?.name, forKey: Defaults.NAME)
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
        guard oldProfile != profile else {
            onBackPressed(sender)
            return
        }
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        let defaults = UserDefaults.standard
        guard let token = defaults.string(forKey: Defaults.TOKEN) else {
            return
        }
        let name = profile?.name?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let summery = profile?.description?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let email = profile?.email?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let website = profile?.website?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
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
            var postData: [String: Any] = ["ui": defaults.integer(forKey: Defaults.USER_ID),
                                           "lk": token,
                                           "dt": SERVICE_DEVICE_TYPE,
                                           "userName": name,
                                           "email": email,
                                           "description": summery,
                                           "phoneNo": phone,
                                           "gender": gender.rawValue]
            if UserDefaults.standard.integer(forKey: Defaults.USER_GROUP) == 1 {
                if !website.isEmpty {
                    if let url = URL(string: website), UIApplication.shared.canOpenURL(url) {
                        postData["webLink"] = url.absoluteString
                    } else {
                        presentDialog(title: "alert_account_update_fail_title".localized, message: "alert_account_update_fail_website_invaid".localized) {
                            action in
                            self.website.becomeFirstResponder()
                        }
                        return
                    }
                } else {
                    postData["webLink"] = website
                }
            }
            if let type = UserType(rawValue: defaults.integer(forKey: Defaults.USER_TYPE)),
               type == .email {
                postData["userCode"] = email
            } else {
                postData["userCode"] = defaults.string(forKey: Defaults.THIRD_PARTY_ID)
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
                        self.presentErrorDialog(message: "alert_network_unreachable_content".localized)
                        return
                    }
                    defaults.set(name, forKey: Defaults.NAME)
                    defaults.set(email, forKey: Defaults.EMAIL)
                    if let profile = self.profile {
                        self.delegate?.accountEdit(profileDidChange: profile)
                    }
                    self.onBackPressed(sender)
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.presentErrorDialog(message: error.localizedDescription)
                }
            }
        }
    }

    @IBAction func pickImage(_ sender: Any) {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        if let presention = alert.popoverPresentationController {
            presention.sourceView = profileImage
            presention.sourceRect = profileImage.bounds
        }
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
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }

    @IBAction func pickGender(_ sender: UIButton) {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        if let presention = alert.popoverPresentationController {
            presention.sourceView = sender
            presention.sourceRect = CGRect(origin: CGPoint(x: -15, y: 0), size: CGSize(width: 0, height: sender.bounds.height))
        }
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            alert.addAction(UIAlertAction(title: "gender_male".localized, style: .default) {
                action in
                self.profile?.gender = .male
                self.gender.setTitle(action.title, for: .normal)
            })
        }
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            alert.addAction(UIAlertAction(title: "gender_female".localized, style: .default) {
                action in
                self.profile?.gender = .female
                self.gender.setTitle(action.title, for: .normal)
            })
        }
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            alert.addAction(UIAlertAction(title: "gender_unspecified".localized, style: .default) {
                action in
                self.profile?.gender = .unspecified
                self.gender.setTitle(action.title, for: .normal)
            })
        }
        alert.addAction(UIAlertAction(title: "alert_button_cancel".localized, style: .cancel))
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }

    internal func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String: Any]) {
        if let image = info[UIImagePickerControllerOriginalImage] as? UIImage {
            guard let token = UserDefaults.standard.string(forKey: Defaults.TOKEN) else {
                return
            }
            if let imageData = UIImageJPEGRepresentation(image, 0) {
                let userId = UserDefaults.standard.integer(forKey: Defaults.USER_ID)
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
                                            self.presentErrorDialog(message: "alert_network_unreachable_content".localized)
                                            return
                                        }
                                        if let imageUrl = URL(string: Service.filePath(withSubPath: data["profilePicture"] as? String)) {
                                            UserDefaults.standard.set(imageUrl, forKey: Defaults.IMAGE)
                                            self.delegate?.accountEdit(imageDidChange: image)
                                            self.profileImage.image = image
                                            self.loading.hide(true)
                                        } else {
                                            self.presentErrorDialog(message: data["msg"] as? String)
                                        }
                                    case .failure(let error):
                                        if let error = error as? URLError, error.code == .cancelled {
                                            return
                                        }
                                        self.presentErrorDialog(message: error.localizedDescription)
                                    }
                                })
                            case .failure(let error):
                                if let error = error as? URLError, error.code == .cancelled {
                                    return
                                }
                                self.presentErrorDialog(message: error.localizedDescription)
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

    internal func textViewDidChange(_ textView: UITextView) {
        profile?.description = textView.text.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    @IBAction func textFieldTextDidChange(_ sender: UITextField) {
        switch sender {
        case name:
            profile?.name = sender.text
        case summery:
            profile?.description = sender.text
        case email:
            profile?.email = sender.text
        case website:
            profile?.website = sender.text
        case phone:
            profile?.phone = sender.text
        default:
            return
        }
    }

    @IBAction func didTapBackground(_ sender: Any) {
        view.endEditing(false)
    }

    private func setViewData() {
        name.text = profile?.name
        summery.text = profile?.description
        email.text = profile?.email
        website.text = profile?.website
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
}

protocol AccountEditViewControllerDelegate {
    func accountEdit(profileDidChange profile: Profile)

    func accountEdit(imageDidChange image: UIImage)
}
