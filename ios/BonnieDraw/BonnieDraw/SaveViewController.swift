//
//  SaveViewController.swift
//  BonnieDraw
//
//  Created by Professor on 30/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class SaveViewController: BackButtonViewController, UITextViewDelegate, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var workTitle: UITextField!
    @IBOutlet weak var workDescription: UITextView!
    var workThumbnailData: Data?
    var workFileUrl: URL?
    var workCategory: WorkCategory?
    private var viewOriginY: CGFloat = 0
    private var keyboardOnScreen = false
    private var dataRequest: DataRequest?

    override func viewDidLoad() {
        if let workThumbnailData = workThumbnailData {
            thumbnail.image = UIImage(data: workThumbnailData)
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: NSNotification.Name.UIKeyboardDidShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide), name: NSNotification.Name.UIKeyboardDidHide, object: nil)
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
            view.frame.origin.y -= keyboardSize.height / 3
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

    private func showErrorMessage(message: String?) {
        presentDialog(title: "alert_save_fail_title".localized, message: message)
        loading.hide(true)
    }

    @IBAction func save(_ sender: UIBarButtonItem) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let workThumbnailData = workThumbnailData, let workFileUrl = workFileUrl else {
            return
        }
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
        } else if let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
                  let token = UserDefaults.standard.string(forKey: Default.TOKEN) {
            sender.isEnabled = false
            progressBar.progress = 0
            loading.hide(false)
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.WORK_SAVE),
                    method: .post,
                    parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "ac": 1, "privacyType": 1, "title": title, "description": description],
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
                                multipartFormData.append(workThumbnailData, withName: "file", fileName: "\(workId).png", mimeType: "image/png")
                            },
                            to: Service.standard(withPath: Service.FILE_UPLOAD) + "?ui=\(userId)&lk=\(token)&dt=\(SERVICE_DEVICE_TYPE)&wid=\(workId)&ftype=\(FileType.png.rawValue)",
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
                                                        multipartFormData.append(workFileUrl, withName: "file", fileName: "\(workId).bdw", mimeType: "")
                                                    },
                                                    to: Service.standard(withPath: Service.FILE_UPLOAD) + "?ui=\(userId)&lk=\(token)&dt=\(SERVICE_DEVICE_TYPE)&wid=\(workId)&ftype=\(FileType.bdw.rawValue)",
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
                                                                    self.navigationController?.popViewController(animated: true)
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

    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if text == "\n" {
            textView.resignFirstResponder()
            return false
        }
        return true
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
