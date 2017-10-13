//
//  SaveViewController.swift
//  BonnieDraw
//
//  Created by Professor on 30/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SaveViewController: BackButtonViewController, UITextViewDelegate, UITextFieldDelegate, CategoryViewControllerDelegate {
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var workDescription: UITextView!
    @IBOutlet weak var workTitle: UITextField!
    @IBOutlet weak var category: UIButton!
    var workThumbnailData: Data?
    var workFileData: Data?
    var workCategory: WorkCategory?
    let client = RestClient(scheme: Service.SCHEME, host: Service.HOST)

    override func viewDidLoad() {
        workDescription.layer.borderColor = UIColor.lightGray.withAlphaComponent(0.5).cgColor
        if let workThumbnailData = workThumbnailData {
            thumbnail.image = UIImage(data: workThumbnailData)
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? CategoryViewController {
            controller.delegate = self
        }
    }

    @IBAction func save(_ sender: Any) {
        guard let workThumbnailData = workThumbnailData else {
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
                client.components.path = Service.WORK_SAVE
                client.getResponse(data: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "ac": 1, "privacyType": 1, "title": title, "description": description]) {
                    success, data in
                    guard success, data?["res"] as? Int == 1, let workId = data?["wid"] as? Int else {
                        self.presentDialog(title: "alert_save_fail_title".localized, message: "app_network_unreachable_content".localized)
                        return
                    }
                    self.client.components.path = Service.FILE_UPLOAD
                    let queries = [URLQueryItem(name: "ui", value: userId),
                                   URLQueryItem(name: "lk", value: token),
                                   URLQueryItem(name: "dt", value: "\(SERVICE_DEVICE_TYPE)"),
                                   URLQueryItem(name: "wid", value: "\(workId)"),
                                   URLQueryItem(name: "ftype", value: "\(FileType.png.rawValue)")]
                    self.client.uploadFile(queries: queries, fileData: workThumbnailData) {
                        success, data in
                        Logger.d(data)
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
