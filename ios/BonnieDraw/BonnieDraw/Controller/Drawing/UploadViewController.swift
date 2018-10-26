//
//  UploadViewController.swift
//  BonnieDraw
//
//  Created by Professor on 30/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire
import DropDown

class UploadViewController: BackButtonViewController, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var workTitle: UITextField!
    @IBOutlet weak var workLinkLabel: UILabel!
    @IBOutlet weak var workLink: UITextField!
    @IBOutlet weak var workLinkDivider: UIView!
    @IBOutlet weak var workLinkHeight: NSLayoutConstraint!
    @IBOutlet weak var workLinkDividerHeight: NSLayoutConstraint!
    @IBOutlet weak var workDescription: UITextView!
    @IBOutlet weak var accessLabel: UILabel!
    private var viewOriginCenterY: CGFloat?
    private var dataRequest: DataRequest?
    private let dropDownItems: [(access: AccessControl, title: String)] = [(.publicAccess, "access_control_public".localized),
                                                                           (.contactAccess, "access_control_contact".localized),
                                                                           (.privateAccess, "access_control_private".localized)]
    private var accessControl = AccessControl.publicAccess
    let dropDown = DropDown()
    var workThumbnail: UIImage?

    override func viewDidLoad() {
        dropDown.dataSource = dropDownItems.map() {
            item in
            return item.title
        }
        dropDown.selectRow(at: 0)
        dropDown.selectionAction = {
            index, text in
            self.accessLabel.text = text
            self.accessControl = self.dropDownItems[index].access
        }
        thumbnail.image = workThumbnail
        if UserDefaults.standard.integer(forKey: Defaults.USER_GROUP) == 1 {
            workLinkLabel.isHidden = false
            workLink.isHidden = false
            workLinkDivider.isHidden = false
            workLinkHeight.constant = 44
            workLinkDividerHeight.constant = 1
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
        view.endEditing(true)
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WebViewController {
            controller.url = URL(string: Service.TERM_OF_USE + "?lang=\(Locale.current.languageCode ?? "")-\(Locale.current.regionCode ?? "")")
        }
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size,
           let animationDuration = notification.userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber,
           let animationCurve = notification.userInfo?[UIResponder.keyboardAnimationCurveUserInfoKey] as? NSNumber {
            if viewOriginCenterY == nil {
                viewOriginCenterY = view.center.y
            }
            UIView.animate(withDuration: animationDuration.doubleValue, delay: 0, options: [UIView.AnimationOptions(rawValue: UInt(animationCurve.intValue))], animations: {
                if let viewOriginCenterY = self.viewOriginCenterY {
                    self.view.center.y = viewOriginCenterY - keyboardSize.height / 3
                }
            })
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if let animationDuration = notification.userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber,
           let animationCurve = notification.userInfo?[UIResponder.keyboardAnimationCurveUserInfoKey] as? NSNumber {
            UIView.animate(withDuration: animationDuration.doubleValue, delay: 0, options: [UIView.AnimationOptions(rawValue: UInt(animationCurve.intValue))], animations: {
                if let viewOriginCenterY = self.viewOriginCenterY {
                    self.view.center.y = viewOriginCenterY
                }
            })
        }
    }

    private func presentErrorDialog(message: String?) {
        presentAlert(title: "alert_save_fail_title".localized, message: message)
        loading.hide(true)
    }

    @IBAction func accessControl(_ sender: UIButton) {
        dropDown.anchorView = sender.superview
        dropDown.show()
    }

    @IBAction func post(_ sender: UIBarButtonItem) {
        guard AppDelegate.reachability.connection != .none else {
            presentAlert(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        guard let workThumbnail = workThumbnail,
              let workThumbnailData = workThumbnail.jpegData(compressionQuality: 1),
              let token = UserDefaults.standard.string(forKey: Defaults.TOKEN) else {
            return
        }
        let userId = UserDefaults.standard.integer(forKey: Defaults.USER_ID)
        let link = workLink.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let description = workDescription.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let title = workTitle.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        if description.isEmpty {
            presentAlert(title: "alert_save_fail_title".localized, message: "alert_save_fail_description_empty".localized) {
                action in
                self.workDescription.becomeFirstResponder()
            }
        } else if title.isEmpty {
            presentAlert(title: "alert_save_fail_title".localized, message: "alert_save_fail_name_empty".localized) {
                action in
                self.workTitle.becomeFirstResponder()
            }
        } else {
            var postData: [String: Any] = ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "ac": 1, "privacyType": accessControl.rawValue, "title": title, "description": description]
            if UserDefaults.standard.integer(forKey: Defaults.USER_GROUP) == 1 {
                if !link.isEmpty {
                    if let url = URL(string: link), UIApplication.shared.canOpenURL(url) {
                        postData["commodityUrl"] = url.absoluteString
                    } else {
                        presentAlert(title: "alert_save_fail_title".localized, message: "alert_account_update_fail_website_invaid".localized) {
                            action in
                            self.workDescription.becomeFirstResponder()
                        }
                        return
                    }
                }
            }
            sender.isEnabled = false
            progressBar.progress = 0
            loading.hide(false)
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.WORK_SAVE),
                    method: .post,
                    parameters: postData,
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let workId = data["wid"] as? Int else {
                        self.presentErrorDialog(message: "alert_network_unreachable_content".localized)
                        sender.isEnabled = true
                        return
                    }
                    Alamofire.upload(
                            multipartFormData: {
                                multipartFormData in
                                multipartFormData.append(workThumbnailData, withName: "file", fileName: "\(workId).jpg", mimeType: "image/jpg")
                            },
                            to: Service.standard(withPath: Service.FILE_UPLOAD) + "?ui=\(userId)&lk=\(token)&dt=\(SERVICE_DEVICE_TYPE)&fn=1&wid=\(workId)&ftype=\(FileType.png.rawValue)",
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
                                                sender.isEnabled = true
                                                return
                                            }
                                            Alamofire.upload(
                                                    multipartFormData: {
                                                        multipartFormData in
                                                        multipartFormData.append(FileUrl.DRAFT, withName: "file", fileName: "\(workId).bdw", mimeType: "")
                                                    },
                                                    to: Service.standard(withPath: Service.FILE_UPLOAD) + "?ui=\(userId)&lk=\(token)&dt=\(SERVICE_DEVICE_TYPE)&fn=1&wid=\(workId)&ftype=\(FileType.bdw.rawValue)",
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
                                                                        sender.isEnabled = true
                                                                        return
                                                                    }
                                                                    self.navigationController?.dismiss(animated: true)
                                                                    NotificationCenter.default.post(name: .profileChanged, object: nil)
                                                                case .failure(let error):
                                                                    if let error = error as? URLError, error.code == .cancelled {
                                                                        return
                                                                    }
                                                                    self.presentErrorDialog(message: error.localizedDescription)
                                                                    sender.isEnabled = true
                                                                }
                                                            })
                                                        case .failure(let error):
                                                            if let error = error as? URLError, error.code == .cancelled {
                                                                return
                                                            }
                                                            self.presentErrorDialog(message: error.localizedDescription)
                                                            sender.isEnabled = true
                                                        }
                                                    })
                                        case .failure(let error):
                                            if let error = error as? URLError, error.code == .cancelled {
                                                return
                                            }
                                            self.presentErrorDialog(message: error.localizedDescription)
                                            sender.isEnabled = true
                                        }
                                    })
                                case .failure(let error):
                                    if let error = error as? URLError, error.code == .cancelled {
                                        return
                                    }
                                    self.presentErrorDialog(message: error.localizedDescription)
                                    sender.isEnabled = true
                                }
                            })
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.presentErrorDialog(message: error.localizedDescription)
                    sender.isEnabled = true
                }
            }
        }
    }

    @IBAction func didTapBackground(_ sender: Any) {
        view.endEditing(false)
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
