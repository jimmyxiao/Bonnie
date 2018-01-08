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
    @IBOutlet weak var workDescription: UITextView!
    @IBOutlet weak var accessLabel: UILabel!
    private var keyboardOnScreen = false
    private var viewOriginY: CGFloat = 0
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
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if !keyboardOnScreen, let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size {
            keyboardOnScreen = true
            viewOriginY = view.frame.origin.y
            view.frame.origin.y -= keyboardSize.height / 3
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if keyboardOnScreen {
            keyboardOnScreen = false
            view.frame.origin.y = viewOriginY
        }
    }

    private func showErrorMessage(message: String?) {
        presentDialog(title: "alert_save_fail_title".localized, message: message)
        loading.hide(true)
    }

    @IBAction func accessControl(_ sender: UIButton) {
        dropDown.anchorView = sender.superview
        dropDown.show()
    }

    @IBAction func post(_ sender: UIBarButtonItem) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let workThumbnail = workThumbnail,
              let workThumbnailData = UIImageJPEGRepresentation(workThumbnail, 1),
              let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        let userId = UserDefaults.standard.integer(forKey: Default.USER_ID)
        let description = self.workDescription.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let title = self.workTitle.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        if description.isEmpty {
            presentDialog(title: "alert_save_fail_title".localized, message: "alert_save_fail_description_empty".localized) {
                action in
                self.workDescription.becomeFirstResponder()
            }
        } else if title.isEmpty {
            presentDialog(title: "alert_save_fail_title".localized, message: "alert_save_fail_name_empty".localized) {
                action in
                self.workTitle.becomeFirstResponder()
            }
        } else {
            sender.isEnabled = false
            progressBar.progress = 0
            loading.hide(false)
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.WORK_SAVE),
                    method: .post,
                    parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "ac": 1, "privacyType": accessControl.rawValue, "title": title, "description": description],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let workId = data["wid"] as? Int else {
                        self.showErrorMessage(message: "app_network_unreachable_content".localized)
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
                                                self.showErrorMessage(message: "app_network_unreachable_content".localized)
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
                                                                        self.showErrorMessage(message: "app_network_unreachable_content".localized)
                                                                        sender.isEnabled = true
                                                                        return
                                                                    }
                                                                    self.navigationController?.dismiss(animated: true)
                                                                case .failure(let error):
                                                                    if let error = error as? URLError, error.code == .cancelled {
                                                                        return
                                                                    }
                                                                    self.showErrorMessage(message: error.localizedDescription)
                                                                    sender.isEnabled = true
                                                                }
                                                            })
                                                        case .failure(let error):
                                                            if let error = error as? URLError, error.code == .cancelled {
                                                                return
                                                            }
                                                            self.showErrorMessage(message: error.localizedDescription)
                                                            sender.isEnabled = true
                                                        }
                                                    })
                                        case .failure(let error):
                                            if let error = error as? URLError, error.code == .cancelled {
                                                return
                                            }
                                            self.showErrorMessage(message: error.localizedDescription)
                                            sender.isEnabled = true
                                        }
                                    })
                                case .failure(let error):
                                    if let error = error as? URLError, error.code == .cancelled {
                                        return
                                    }
                                    self.showErrorMessage(message: error.localizedDescription)
                                    sender.isEnabled = true
                                }
                            })
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.showErrorMessage(message: error.localizedDescription)
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
