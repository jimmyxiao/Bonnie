//
//  SaveViewController.swift
//  BonnieDraw
//
//  Created by Professor on 30/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SaveViewController: BackButtonViewController, UITextViewDelegate, UITextFieldDelegate {
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var workDescription: UITextView!
    @IBOutlet weak var workTitle: UITextField!
    var keyboardOnScreen = false
    var workThumbnailData: Data?
    var workFileData: Data?
    let client = RestClient.standard(withPath: Service.WORK_SAVE)

    override func viewDidLoad() {
        workDescription.layer.borderColor = UIColor.lightGray.withAlphaComponent(0.5).cgColor
        if let workThumbnailData = workThumbnailData {
            thumbnail.image = UIImage(data: workThumbnailData)
        }
    }

    override func viewWillAppear(_ animated: Bool) {
//        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
//        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: NSNotification.Name.UIKeyboardDidShow, object: nil)
//        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
//        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide), name: NSNotification.Name.UIKeyboardDidHide, object: nil)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if !keyboardOnScreen, let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size {
            view.frame.origin.y -= keyboardSize.height * 0.3
        }
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if keyboardOnScreen {
            view.frame.origin.y = 0
        }
    }

    @objc func keyboardDidShow(_ notification: Notification) {
        keyboardOnScreen = true
    }

    @objc func keyboardDidHide(_ notification: Notification) {
        keyboardOnScreen = false
    }

    @IBAction func save(_ sender: Any) {
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
        } else if let id = UserDefaults.standard.string(forKey: Default.USER_ID),
                  let token = UserDefaults.standard.string(forKey: Default.TOKEN) {
            client.getResponse(data: ["ui": id, "lk": token, "dt": SERVICE_DEVICE_TYPE, "ac": 1, "privacyType": 1, "title": title, "description": description, "languageId": 1, "countryId": 1]) {
                success, data in
                if success {
                } else {
                }
            }
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
}
