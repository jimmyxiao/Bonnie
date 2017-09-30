//
//  SaveViewController.swift
//  BonnieDraw
//
//  Created by Professor on 30/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SaveViewController: UIViewController, UITextFieldDelegate, UITextViewDelegate {
    @IBOutlet weak var alertBox: UIVisualEffectView!
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var workDescription: UITextView!
    var delegate: SaveViewControllerDelegate?
    var keyboardOnScreen = false

    override func viewDidLoad() {
        workDescription.layer.borderColor = UIColor.lightGray.withAlphaComponent(0.5).cgColor
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: NSNotification.Name.UIKeyboardDidShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide), name: NSNotification.Name.UIKeyboardDidHide, object: nil)
    }

    override func viewDidAppear(_ animated: Bool) {
        alertBox.transform = CGAffineTransform(scaleX: 1.2, y: 1.2)
        UIView.animate(withDuration: 0.3) {
            self.alertBox.alpha = 1
            self.alertBox.transform = CGAffineTransform(scaleX: 1, y: 1)
        }
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

    @IBAction func cancel(_ sender: Any) {
        UIView.animate(withDuration: 0.3, animations: {
            self.view.alpha = 0
        }) {
            finished in
            self.dismiss(animated: false)
        }
    }

    @IBAction func save(_ sender: Any) {
        let name = self.name.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let description = self.workDescription.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        if name.isEmpty {
            presentDialog(title: "alert_save_fail_title".localized, message: "alert_save_fail_name_empty".localized) {
                action in
                self.name.becomeFirstResponder()
            }
        } else if description.isEmpty {
            presentDialog(title: "alert_save_fail_title".localized, message: "alert_save_fail_description_empty".localized) {
                action in
                self.workDescription.becomeFirstResponder()
            }
        } else {
            delegate?.save(with: name, description: description, category: "")
            UIView.animate(withDuration: 0.3, animations: {
                self.view.alpha = 0
            }) {
                finished in
                self.dismiss(animated: false)
            }
        }
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == name {
            workDescription.becomeFirstResponder()
            return false
        }
        textField.resignFirstResponder()
        return true
    }
}

protocol SaveViewControllerDelegate {
    func save(with name: String, description: String, category: String)
}
