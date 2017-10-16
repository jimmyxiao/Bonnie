//
//  SaveViewController.swift
//  BonnieDraw
//
//  Created by Professor on 30/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class SaveViewController: BackButtonViewController, UITextViewDelegate, UITextFieldDelegate, CategoryViewControllerDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var workDescription: UITextView!
    @IBOutlet weak var workTitle: UITextField!
    @IBOutlet weak var category: UIButton!
    var workThumbnailData: Data?
    var workFileData: Data?
    var workCategory: WorkCategory?
    private var dataRequest: DataRequest?

    override func viewDidLoad() {
        workDescription.layer.borderColor = UIColor.lightGray.withAlphaComponent(0.5).cgColor
        if let workThumbnailData = workThumbnailData {
            thumbnail.image = UIImage(data: workThumbnailData)
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    private func showErrorMessage(message: String?) {
        presentDialog(title: "alert_save_fail_title".localized, message: message)
        loading.hide(true)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? CategoryViewController {
            controller.delegate = self
        }
    }

    @IBAction func save(_ sender: UIBarButtonItem) {
        guard AppDelegate.reachability.isReachable else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let workThumbnailData = workThumbnailData, let workFileData = workFileData else {
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
        } else if let category = workCategory {
            if let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
               let token = UserDefaults.standard.string(forKey: Default.TOKEN) {
                sender.isEnabled = false
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
                                            Logger.d(progress.fractionCompleted)
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
                                                            multipartFormData.append(workFileData, withName: "file", fileName: "\(workId).bdw", mimeType: "")
                                                        },
                                                        to: Service.standard(withPath: Service.FILE_UPLOAD) + "?ui=\(userId)&lk=\(token)&dt=\(SERVICE_DEVICE_TYPE)&wid=\(workId)&ftype=\(FileType.bdw.rawValue)",
                                                        encodingCompletion: {
                                                            encodingResult in
                                                            switch encodingResult {
                                                            case .success(let upload, _, _):
                                                                self.dataRequest = upload.uploadProgress(closure: {
                                                                    progress in
                                                                    Logger.d(progress.fractionCompleted)
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
                                                                        self.showErrorMessage(message: error.localizedDescription)
                                                                        sender.isEnabled = true
                                                                    }
                                                                })
                                                            case .failure(let error):
                                                                self.showErrorMessage(message: error.localizedDescription)
                                                                sender.isEnabled = true
                                                            }
                                                        })
                                            case .failure(let error):
                                                self.showErrorMessage(message: error.localizedDescription)
                                                sender.isEnabled = true
                                            }
                                        })
                                    case .failure(let error):
                                        self.showErrorMessage(message: error.localizedDescription)
                                        sender.isEnabled = true
                                    }
                                })
                    case .failure(let error):
                        self.showErrorMessage(message: error.localizedDescription)
                        sender.isEnabled = true
                    }
                }
            }
        } else {
            presentDialog(title: "alert_save_fail_title".localized, message: "alert_save_fail_category_empty".localized)
        }
    }

    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if text == "\n" {
            workTitle.becomeFirstResponder()
            return false
        }
        return true
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    func category(didSelectCategory category: WorkCategory) {
        workCategory = category
        self.category.setTitle(category.name, for: .normal)
    }
}
